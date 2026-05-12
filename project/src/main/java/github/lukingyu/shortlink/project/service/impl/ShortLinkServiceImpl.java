package github.lukingyu.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.lukingyu.shortlink.base.constant.RedisCacheConstant;
import github.lukingyu.shortlink.base.entity.dto.req.ShortLinkPageReqDTO;
import github.lukingyu.shortlink.base.entity.dto.req.ShortLinkUpdateReqDTO;
import github.lukingyu.shortlink.base.entity.dto.resp.ShortLinkGroupCountQueryRespDTO;
import github.lukingyu.shortlink.base.entity.dto.resp.ShortLinkPageRespDTO;
import github.lukingyu.shortlink.base.entity.enums.VailDateTypeEnum;
import github.lukingyu.shortlink.base.entity.exception.ServiceException;
import github.lukingyu.shortlink.base.entity.table.ShortLinkDO;
import github.lukingyu.shortlink.base.entity.table.ShortLinkGotoDO;
import github.lukingyu.shortlink.base.tool.HashUtil;
import github.lukingyu.shortlink.base.entity.dto.req.ShortLinkCreateReqDTO;
import github.lukingyu.shortlink.base.entity.dto.resp.ShortLinkCreateRespDTO;
import github.lukingyu.shortlink.project.mapper.ShortLinkGotoMapper;
import github.lukingyu.shortlink.project.mapper.ShortLinkMapper;
import github.lukingyu.shortlink.project.service.ShortLinkService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortLinkSuffix = generateSuffix(requestParam);
        // 构造完整 短链接 路径
        String fullShortUrl = StrBuilder.create(requestParam.getDomain())
                .append("/")
                .append(shortLinkSuffix)
                .toString();
        // 全新实体
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(requestParam.getDomain())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .createdType(requestParam.getCreatedType())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .describe(requestParam.getDescribe())
                .shortUri(shortLinkSuffix)
                .enableStatus(0)
                .fullShortUrl(fullShortUrl)
                .build();

        // 全新跳转关系实体
        ShortLinkGotoDO linkGotoDO = ShortLinkGotoDO.builder()
                .fullShortUrl(fullShortUrl)
                .gid(requestParam.getGid())
                .build();

        try {
            baseMapper.insert(shortLinkDO);
            shortLinkGotoMapper.insert(linkGotoDO);
        } catch (DuplicateKeyException ex) {
            // 多线程高并发下，很有可能多个线程生成完全相同的短链接后缀
            // 插入数据库时，由于后缀有唯一索引，抛出异常
            log.warn("短链接：{} 重复入库", fullShortUrl);
            throw new ServiceException("短链接生成重复");
        }
        // 当前线程插入成功，将 此后缀放入布隆过滤器，代表此后缀已被使用
        shortUriCreateCachePenetrationBloomFilter.add(shortLinkSuffix);
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl("http://" + shortLinkDO.getFullShortUrl())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }

    private String generateSuffix(ShortLinkCreateReqDTO requestParam) {
        int customGenerateCount = 0;
        String shortUri;
        while (true) {
            if (customGenerateCount > 10) {
                throw new ServiceException("短链接频繁生成，请稍后再试");
            }
            String originUrl = requestParam.getOriginUrl();
            // 原始url加上时间戳，计算哈希字符串
            String salt = originUrl + System.currentTimeMillis();
            shortUri = HashUtil.hashToBase62(salt);
            // 布隆过滤器中不存在，则该哈希字符串可以使用，返回即可
            if (!shortUriCreateCachePenetrationBloomFilter.contains(requestParam.getDomain() + "/" + shortUri)) {
                break;
            }
            customGenerateCount++;
        }
        return shortUri;
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getEnableStatus, 0)
                .eq(ShortLinkDO::getDelFlag, 0)
                .orderByDesc(ShortLinkDO::getCreateTime);
        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(requestParam, queryWrapper);
        return resultPage.convert(each -> {
            ShortLinkPageRespDTO bean = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
            bean.setDomain("http://" + bean.getDomain());
            return bean;
        });
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> gidList) {
        QueryWrapper<ShortLinkDO> queryWrapper = Wrappers.query(new ShortLinkDO())
                .select("gid as gid, count(*) as shortLinkCount")
                .in("gid", gidList)
                .eq("enable_status", 0)
                .groupBy("gid");
        List<Map<String, Object>> shortLinkDOList = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(shortLinkDOList, ShortLinkGroupCountQueryRespDTO.class);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {

        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = new LambdaUpdateWrapper<ShortLinkDO>()
                .set(ShortLinkDO::getOriginUrl, requestParam.getOriginUrl())
                .set(ShortLinkDO::getGid, requestParam.getGid())
                .set(ShortLinkDO::getValidDateType, requestParam.getValidDateType())
                .set(ShortLinkDO::getValidDate, Integer.valueOf(VailDateTypeEnum.PERMANENT.getType()).equals(requestParam.getValidDateType()) ? null : requestParam.getValidDate())
                .set(ShortLinkDO::getDescribe, requestParam.getDescribe())
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0);

        update(updateWrapper);

    }

    @Override
    public void redirect(String shortUri, ServletRequest request, ServletResponse response) throws IOException {
        String serverName = request.getServerName();
        String fullShortUrl = serverName + "/" + shortUri;

        // 缓存中存在该短链接的真实跳转关系，则直接跳转至原始url
        String originalUrl = stringRedisTemplate.opsForValue().get(String.format(RedisCacheConstant.SHORT_LINK_GOTO_KEY, fullShortUrl));
        if (StrUtil.isNotBlank(originalUrl)) {
            ((HttpServletResponse) response).sendRedirect(originalUrl);
            return;
        }

        // 利用布隆过滤器，判断该短链接 是否创建过，不存在则直接返回，100%准确
        boolean contains = shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl);
        if (!contains) {
            return;
        }
        // 不存在跳转关系的短链接，缓存空值，防止恶意请求一直请求
        String gotoIsNullShortLink = stringRedisTemplate.opsForValue().get(String.format(RedisCacheConstant.SHORT_LINK_NULL_GOTO_KEY, fullShortUrl));
        if (StrUtil.isNotBlank(gotoIsNullShortLink)) {
            return;
        }

        // 短链接跳转，公共锁。目的，不让所有请求都去查数据库
        RLock lock = redissonClient.getLock(RedisCacheConstant.SHORT_LINK_REDIRECT_LOCK_KEY);
        lock.lock();
        try {
            // 拿到锁，二次查询缓存，存在跳转关系则直接跳转。
            String originalUrl2 = stringRedisTemplate.opsForValue().get(String.format(RedisCacheConstant.SHORT_LINK_GOTO_KEY, fullShortUrl));
            if (StrUtil.isNotBlank(originalUrl2)) {
                ((HttpServletResponse) response).sendRedirect(originalUrl2);
                return;
            }
            LambdaQueryWrapper<ShortLinkGotoDO> linkGotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
            ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(linkGotoQueryWrapper);
            if (shortLinkGotoDO == null) {
                // 不存在跳转关系，先缓存空值，防止后续一直请求
                stringRedisTemplate.opsForValue().set(String.format(RedisCacheConstant.SHORT_LINK_NULL_GOTO_KEY, fullShortUrl), "-", 3, TimeUnit.MINUTES);
                return;
            }
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, shortLinkGotoDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);
            if (shortLinkDO != null) {
                // 将跳转关系 放入缓存
                stringRedisTemplate.opsForValue().set(String.format(RedisCacheConstant.SHORT_LINK_GOTO_KEY, fullShortUrl), shortLinkDO.getOriginUrl());
                // 重定向
                ((HttpServletResponse) response).sendRedirect(shortLinkDO.getOriginUrl());
            }
        } finally {
            lock.unlock();
        }
    }
}