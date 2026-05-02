package github.lukingyu.shortlink.base.mvc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.lukingyu.shortlink.base.mvc.mapper.GroupMapper;
import github.lukingyu.shortlink.base.mvc.service.GroupService;
import github.lukingyu.shortlink.base.entity.table.GroupDO;
import github.lukingyu.shortlink.base.tool.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    @Override
    public void saveGroup(String groupName) {
        String gid;

        do {
            gid = RandomGenerator.generateRandom();
        } while (existGid(gid));

        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .name(groupName)
                // TODO 用户名
                .username(null)
                .sortOrder(0)
                .build();
        baseMapper.insert(groupDO);
    }

    private boolean existGid(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                // TODO 用户名待设置
                .eq(GroupDO::getUsername, null);

        GroupDO existedGroup = baseMapper.selectOne(queryWrapper);
        return existedGroup != null;
    }
}