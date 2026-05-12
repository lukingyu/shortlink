package github.lukingyu.shortlink.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import github.lukingyu.shortlink.base.entity.dto.req.ShortLinkRecycleBinPageReqDTO;
import github.lukingyu.shortlink.base.entity.dto.resp.ShortLinkPageRespDTO;
import github.lukingyu.shortlink.base.entity.result.Result;

public interface RecycleBinService {

    /**
     * 分页查询回收站短链接
     *
     * @param requestParam 请求参数
     * @return 返回参数包装
     */
    Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam);
}