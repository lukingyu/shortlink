package github.lukingyu.shortlink.admin.controller;

import github.lukingyu.shortlink.base.mvc.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
}