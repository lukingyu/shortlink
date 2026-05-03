package github.lukingyu.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import github.lukingyu.shortlink.base.entity.table.ShortLinkDO;
import github.lukingyu.shortlink.project.entity.dto.req.ShortLinkCreateReqDTO;
import github.lukingyu.shortlink.project.entity.dto.resp.ShortLinkCreateRespDTO;

public interface ShortLinkService extends IService<ShortLinkDO> {

    /**
     * 创建短链接
     *
     * @param requestParam 创建短链接请求参数
     * @return 短链接创建信息
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);
}