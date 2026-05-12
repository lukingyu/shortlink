package github.lukingyu.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import github.lukingyu.shortlink.base.entity.dto.req.RecycleBinRecoverReqDTO;
import github.lukingyu.shortlink.base.entity.dto.req.RecycleBinRemoveReqDTO;
import github.lukingyu.shortlink.base.entity.dto.req.RecycleBinSaveReqDTO;
import github.lukingyu.shortlink.base.entity.dto.req.ShortLinkRecycleBinPageReqDTO;
import github.lukingyu.shortlink.base.entity.dto.resp.ShortLinkPageRespDTO;
import github.lukingyu.shortlink.base.entity.table.ShortLinkDO;

public interface RecycleBinService extends IService<ShortLinkDO> {

    /**
     * 保存回收站
     *
     * @param requestParam 请求参数
     */
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);

    /**
     * 分页查询短链接
     *
     * @param requestParam 分页查询短链接请求参数
     * @return 短链接分页返回结果
     */
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam);

    /**
     * 恢复短链接
     *
     * @param requestParam 请求参数
     */
    void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam);

    /**
     * 从回收站移除短链接
     *
     * @param requestParam 移除短链接请求参数
     */
    void removeRecycleBin(RecycleBinRemoveReqDTO requestParam);
}