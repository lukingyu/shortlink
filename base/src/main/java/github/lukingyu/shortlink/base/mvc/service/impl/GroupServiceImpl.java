package github.lukingyu.shortlink.base.mvc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.lukingyu.shortlink.base.biz.user.UserContext;
import github.lukingyu.shortlink.base.entity.dto.req.ShortLinkGroupSortReqDTO;
import github.lukingyu.shortlink.base.entity.dto.req.ShortLinkGroupUpdateReqDTO;
import github.lukingyu.shortlink.base.entity.dto.resp.ShortLinkGroupCountQueryRespDTO;
import github.lukingyu.shortlink.base.entity.dto.resp.ShortLinkGroupRespDTO;
import github.lukingyu.shortlink.base.entity.result.Result;
import github.lukingyu.shortlink.base.entity.table.BaseDO;
import github.lukingyu.shortlink.base.mvc.mapper.GroupMapper;
import github.lukingyu.shortlink.base.mvc.service.GroupService;
import github.lukingyu.shortlink.base.entity.table.GroupDO;
import github.lukingyu.shortlink.base.remote.ShortLinkRemoteService;
import github.lukingyu.shortlink.base.tool.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    // TODO 后续需要替换为 feign client
    private final ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    @Override
    public void saveGroup(String groupName) {
        saveGroup(UserContext.getUsername(), groupName);
    }

    @Override
    public void saveGroup(String username, String groupName) {
        String gid;

        do {
            gid = RandomGenerator.generateRandom();
        } while (existGid(gid, username));

        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .name(groupName)
                .username(UserContext.getUsername())
                .sortOrder(0)
                .build();
        baseMapper.insert(groupDO);
    }

    private boolean existGid(String gid, String username) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, username == null ? UserContext.getUsername() : username);

        GroupDO existedGroup = baseMapper.selectOne(queryWrapper);
        return existedGroup != null;
    }

    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0)
                .orderByDesc(GroupDO::getSortOrder, BaseDO::getUpdateTime);
        List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
        List<ShortLinkGroupRespDTO> respList = BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);
        Result<List<ShortLinkGroupCountQueryRespDTO>> listResult = shortLinkRemoteService.listGroupShortLinkCount(groupDOList.stream().map(GroupDO::getGid).toList());
        if (listResult.isSuccess()) {
            Map<String, ShortLinkGroupCountQueryRespDTO> gidRespMap = listResult.getData().stream().collect(Collectors.toMap(ShortLinkGroupCountQueryRespDTO::getGid, e -> e));
            respList.forEach(resp ->
                Optional.ofNullable(gidRespMap.get(resp.getGid())).ifPresent(value ->
                    value.setShortLinkCount(resp.getShortLinkCount())
                )
            );
        }
        return respList;
    }

    @Override
    public void updateGroup(ShortLinkGroupUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setName(requestParam.getName());
        baseMapper.update(groupDO, updateWrapper);
    }

    @Override
    public void deleteGroup(String gid) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        baseMapper.update(groupDO, updateWrapper);
    }

    @Override
    public void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam) {
        requestParam.forEach(each -> {
            GroupDO groupDO = GroupDO.builder()
                    .sortOrder(each.getSortOrder())
                    .build();
            LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                    .eq(GroupDO::getUsername, UserContext.getUsername())
                    .eq(GroupDO::getGid, each.getGid())
                    .eq(GroupDO::getDelFlag, 0);
            baseMapper.update(groupDO, updateWrapper);
        });
    }
}