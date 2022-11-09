package cn.tedu.csmall.passport.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录的当事人,包含用户名和密码
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginPrincipal implements Serializable {

    private String username;
    private Long id;
}
