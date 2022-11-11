package cn.tedu.csmall.passport.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录的当事人,包含了用户名和id
 * 用于认证成功后,将其封装到Security Context上下文中
 *
 * @Author java.@Wqy
 * @Version 0.0.1
 */
@Data
@NoArgsConstructor // 添加无参构造
@AllArgsConstructor// 添加全参构造
public class LoginPrincipal implements Serializable {

    private String username;
    private Long id;
}
