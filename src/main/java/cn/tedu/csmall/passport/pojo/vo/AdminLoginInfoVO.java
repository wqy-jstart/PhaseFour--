package cn.tedu.csmall.passport.pojo.vo;

import lombok.Data;

import java.util.List;

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
     * 管理员密码(密文)
     */
    private String password;
    /**
     * 是否启用(1=启用,0=禁用)
     */
    private Integer enable;

    /**
     * 权限列表
     */
    private List<String> permissions;

}
