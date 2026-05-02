package github.lukingyu.shortlink.admin.controller;

import github.lukingyu.shortlink.base.entity.dto.req.ShortLinkGroupSaveReqDTO;
import github.lukingyu.shortlink.base.entity.dto.resp.ShortLinkGroupRespDTO;
import github.lukingyu.shortlink.base.entity.result.Result;
import github.lukingyu.shortlink.base.entity.result.Results;
import github.lukingyu.shortlink.base.mvc.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    /**
     * 新增短链接分组
     */
    @PostMapping("/api/short-link/v1/group")
    public Result<Void> save(@RequestBody ShortLinkGroupSaveReqDTO requestParam) {
        groupService.saveGroup(requestParam.getName());
        return Results.success();
    }

    @GetMapping("/api/short-link/v1/group")
    public Result<List<ShortLinkGroupRespDTO>> listGroup() {
        return Results.success(groupService.listGroup());
    }
}