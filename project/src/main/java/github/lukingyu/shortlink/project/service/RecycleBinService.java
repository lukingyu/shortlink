package github.lukingyu.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import github.lukingyu.shortlink.base.entity.dto.req.RecycleBinSaveReqDTO;
import github.lukingyu.shortlink.base.entity.table.ShortLinkDO;

public interface RecycleBinService extends IService<ShortLinkDO> {

    /**
     * 保存回收站
     *
     * @param requestParam 请求参数
     */
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);
}