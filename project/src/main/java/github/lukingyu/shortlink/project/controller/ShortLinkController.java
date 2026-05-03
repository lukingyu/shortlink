package github.lukingyu.shortlink.project.controller;

import github.lukingyu.shortlink.base.entity.result.Result;
import github.lukingyu.shortlink.base.entity.result.Results;
import github.lukingyu.shortlink.project.entity.dto.req.ShortLinkCreateReqDTO;
import github.lukingyu.shortlink.project.entity.dto.resp.ShortLinkCreateRespDTO;
import github.lukingyu.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}