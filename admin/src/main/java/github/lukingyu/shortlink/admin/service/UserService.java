package github.lukingyu.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import github.lukingyu.shortlink.base.entity.dto.resp.UserRespDTO;
import github.lukingyu.shortlink.base.entity.table.UserDO;

public interface UserService extends IService<UserDO> {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户返回实体
     */
    UserRespDTO getUserByUsername(String username);
}