package cn.tedu.csmall.passport.pojo.vo;

import lombok.Data;

/**
 * 这是登录相关的管理员VO类
 *
 * @Author java@Wqy
 * @Version 0.0.1
 */
@Data
public class AdminLoginInfoVO {
    /**
     * 管理员id
     */
    private Long id;
    /**
     * 管理员用户名
     */
    private String username;
    /**
     * 管理员密码
     */
    private String password;
    /**
     * 是否启用
     */
    private Integer enable;
}
