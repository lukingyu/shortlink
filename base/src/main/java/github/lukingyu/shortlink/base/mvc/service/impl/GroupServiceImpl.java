package github.lukingyu.shortlink.base.mvc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.lukingyu.shortlink.base.mvc.mapper.GroupMapper;
import github.lukingyu.shortlink.base.mvc.service.GroupService;
import github.lukingyu.shortlink.base.entity.table.GroupDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

}