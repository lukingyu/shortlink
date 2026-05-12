package github.lukingyu.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import github.lukingyu.shortlink.base.entity.dto.req.ShortLinkPageReqDTO;
import github.lukingyu.shortlink.base.entity.dto.req.ShortLinkUpdateReqDTO;
import github.lukingyu.shortlink.base.entity.dto.resp.ShortLinkGroupCountQueryRespDTO;
import github.lukingyu.shortlink.base.entity.dto.resp.ShortLinkPageRespDTO;
import github.lukingyu.shortlink.base.entity.result.Result;
import github.lukingyu.shortlink.base.entity.result.Results;
import github.lukingyu.shortlink.base.entity.dto.req.ShortLinkCreateReqDTO;
import github.lukingyu.shortlink.base.entity.dto.resp.ShortLinkCreateRespDTO;
import github.lukingyu.shortlink.project.service.ShortLinkService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return Results.success(shortLinkService.createShortLink(requestParam));
    }

    /**
     * 分页查询短链接
     */
    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return Results.success(shortLinkService.pageShortLink(requestParam));
    }

    /**
     * 查询短链接分组内数量
     */
    @GetMapping("/api/short-link/v1/count")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(@RequestParam("requestParam") List<String> gidList) {
        return Results.success(shortLinkService.listGroupShortLinkCount(gidList));
    }

    /**
     * 更新短链接
     */
    @PutMapping("/api/short-link/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        shortLinkService.updateShortLink(requestParam);
        return Results.success();
    }

    /**
     * 短链接跳转
     */
    @GetMapping("/{short-uri}")
    public Result<Void> redirect(@PathVariable("short-uri") String shortUri, ServletRequest request, ServletResponse response) throws IOException {
        shortLinkService.redirect(shortUri, request, response);
        return Results.success();
    }
}