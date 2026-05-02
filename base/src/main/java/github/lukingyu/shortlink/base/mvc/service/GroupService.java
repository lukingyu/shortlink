package github.lukingyu.shortlink.base.mvc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import github.lukingyu.shortlink.base.entity.dto.resp.ShortLinkGroupRespDTO;
import github.lukingyu.shortlink.base.entity.table.GroupDO;

import java.util.List;

public interface GroupService extends IService<GroupDO> {

    /**
     * 新增短链接分组
     *
     * @param groupName 短链接分组名
     */
    void saveGroup(String groupName);

    /**
     * 查询用户短链接分组集合
     *
     * @return 用户短链接分组集合
     */
    List<ShortLinkGroupRespDTO> listGroup();
}