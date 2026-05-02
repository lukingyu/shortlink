package github.lukingyu.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.lukingyu.shortlink.admin.mapper.UserMapper;
import github.lukingyu.shortlink.admin.service.UserService;
import github.lukingyu.shortlink.base.constant.RedisCacheConstant;
import github.lukingyu.shortlink.base.entity.dto.req.UserLoginReqDTO;
import github.lukingyu.shortlink.base.entity.dto.req.UserRegisterReqDTO;
import github.lukingyu.shortlink.base.entity.dto.req.UserUpdateReqDTO;
import github.lukingyu.shortlink.base.entity.dto.resp.UserLoginRespDTO;
import github.lukingyu.shortlink.base.entity.dto.resp.UserRespDTO;
import github.lukingyu.shortlink.base.entity.enums.UserErrorCodeEnum;
import github.lukingyu.shortlink.base.entity.exception.ClientException;
import github.lukingyu.shortlink.base.entity.exception.ServiceException;
import github.lukingyu.shortlink.base.entity.table.UserDO;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }
        UserRespDTO result = new UserRespDTO();
        BeanUtils.copyProperties(userDO, result);
        return result;
    }

    @Override
    public Boolean hasUsername(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        // 如果布隆过滤器存在此用户名，则抛出异常
        if (hasUsername(requestParam.getUsername())) {
            throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
        }

        // 加 分布式锁，防止高并发下同时注册一个不存在的用户名
        RLock lock = redissonClient.getLock(RedisCacheConstant.LOCK_USER_REGISTER_KEY + requestParam.getUsername());

        try {
            if (lock.tryLock()) {
                // 用户名可用
                int insert = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
                // 数据库插入失败
                if (insert <= 0) {
                    throw new ServiceException(UserErrorCodeEnum.USER_SAVE_ERROR);
                }
                // 数据库插入成功，向布隆过滤器记录此用户名
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
            } else {
                // 获取锁失败，说明有其他线程正在注册此用户名，抛出异常
                throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
            }
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void update(UserUpdateReqDTO requestParam) {
        // TODO 验证当前用户名是否为登录用户
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername());
        baseMapper.update(BeanUtil.toBean(requestParam, UserDO.class), updateWrapper);
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getPassword, requestParam.getPassword())
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ClientException("用户不存在");
        }

        Boolean hasLogin = stringRedisTemplate.hasKey(RedisCacheConstant.LOGIN_USER_TOKEN_KEY + requestParam.getUsername());
        if (hasLogin) {
            throw new ClientException("用户已登录");
        }

        /*
          Hash
          Key：login_用户名
          Value：
           Key：token标识
           Val：JSON 字符串（用户信息）
         */

        String token = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put(RedisCacheConstant.LOGIN_USER_TOKEN_KEY + requestParam.getUsername(), token, JSON.toJSONString(userDO));
        stringRedisTemplate.expire(RedisCacheConstant.LOGIN_USER_TOKEN_KEY + requestParam.getUsername(), 30L, TimeUnit.MINUTES);
        return new UserLoginRespDTO(token);
    }

    @Override
    public Boolean checkLogin(String username, String token) {
        return stringRedisTemplate.opsForHash().get(RedisCacheConstant.LOGIN_USER_TOKEN_KEY + username, token) != null;
    }
}