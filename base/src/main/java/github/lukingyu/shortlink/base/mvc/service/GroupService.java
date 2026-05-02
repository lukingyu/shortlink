package github.lukingyu.shortlink.base.mvc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import github.lukingyu.shortlink.base.entity.table.GroupDO;

public interface GroupService extends IService<GroupDO> {

    /**
     * 新增短链接分组
     *
     * @param groupName 短链接分组名
     */
    void saveGroup(String groupName);
}