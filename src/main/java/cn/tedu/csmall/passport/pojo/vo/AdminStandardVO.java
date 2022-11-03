package cn.tedu.csmall.passport.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminStandardVO {
    /**
     * ID
     */
    private Long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 电话号码
     */
    private String phone;
    /**
     * 电子邮箱
     */
    private String email;
    /**
     * 描述
     */
    private String description;
    /**
     * 是否启用
     */
    private Integer enable;
    /**
     * 最后一次登录IP
     */
    private String lastLoginIp;
    /**
     * 登录次数
     */
    private Integer loginCount;
    /**
     * 最后一次登录时间
     */
    private LocalDateTime gmtLastLogin;
}
