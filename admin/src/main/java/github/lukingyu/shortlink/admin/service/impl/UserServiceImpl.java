package github.lukingyu.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.lukingyu.shortlink.admin.mapper.UserMapper;
import github.lukingyu.shortlink.admin.service.UserService;
import github.lukingyu.shortlink.base.entity.dto.resp.UserRespDTO;
import github.lukingyu.shortlink.base.entity.enums.UserErrorCodeEnum;
import github.lukingyu.shortlink.base.entity.exception.ClientException;
import github.lukingyu.shortlink.base.entity.table.UserDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }
        UserRespDTO result = new UserRespDTO();
        BeanUtils.copyProperties(userDO, result);
        return result;
    }
}