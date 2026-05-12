package github.lukingyu.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import github.lukingyu.shortlink.base.entity.dto.req.ShortLinkUpdateReqDTO;
import github.lukingyu.shortlink.base.entity.result.Results;
import github.lukingyu.shortlink.base.remote.ShortLinkRemoteService;
import github.lukingyu.shortlink.base.entity.dto.req.ShortLinkCreateReqDTO;
import github.lukingyu.shortlink.base.entity.dto.req.ShortLinkPageReqDTO;
import github.lukingyu.shortlink.base.entity.dto.resp.ShortLinkCreateRespDTO;
import github.lukingyu.shortlink.base.entity.dto.resp.ShortLinkPageRespDTO;
import github.lukingyu.shortlink.base.entity.result.Result;
import org.springframework.web.bind.annotation.*;

@RestController
public class ShortLinkController {

    /**
     * 后续重构为 SpringCloud Feign 调用
     */
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return shortLinkRemoteService.createShortLink(requestParam);
    }

    /**
     * 分页查询短链接
     */
    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return shortLinkRemoteService.pageShortLink(requestParam);
    }

    /**
     * 更新短链接
     */
    @PatchMapping("/api/short-link/admin/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        shortLinkRemoteService.updateShortLink(requestParam);
        return Results.success();
    }
}