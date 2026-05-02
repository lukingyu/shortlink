package github.lukingyu.shortlink.base.entity.dto.resp;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserLoginRespDTO {

    /**
     * 用户Token
     */
    private final String token;
}