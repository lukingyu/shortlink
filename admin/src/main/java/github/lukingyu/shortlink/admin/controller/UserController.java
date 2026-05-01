package github.lukingyu.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import github.lukingyu.shortlink.admin.service.UserService;
import github.lukingyu.shortlink.base.entity.dto.resp.UserActualRespDTO;
import github.lukingyu.shortlink.base.entity.dto.resp.UserRespDTO;
import github.lukingyu.shortlink.base.entity.result.Result;
import github.lukingyu.shortlink.base.entity.result.Results;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 根据用户名查询用户信息
     */
    @GetMapping("/api/shortlink/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable String username) {
        return Results.success(userService.getUserByUsername(username));
    }

    /**
     * 根据用户名查询无脱敏用户信息
     */
    @GetMapping("/api/shortlink/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getActualUserByUsername(@PathVariable String username) {
        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username), UserActualRespDTO.class));
    }
}