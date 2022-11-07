package cn.tedu.csmall.passport.pojo.dto;

import lombok.Data;

/**
 * 管理员登录的DTO类
 *
 * @java.@Wqy
 * @Version 0.0.1
 */
@Data
public class AdminLoginDTO {
    /**
     * 登录的用户名
     */
    private String username;
    /**
     * 登录的原文密码
     */
    private String password;
}
