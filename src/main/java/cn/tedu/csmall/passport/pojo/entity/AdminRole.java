package cn.tedu.csmall.passport.pojo.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AdminRole implements Serializable {

    /**
     * 数据id
     */
    private Long id;

    /**
     * 管理员id
     */
    private Long adminId;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 数据创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 数据最后修改时间
     */
    private LocalDateTime gmtModified;
}
