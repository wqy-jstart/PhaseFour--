package cn.tedu.csmall.passport.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色的详情类
 */
@Data
public class RoleStandardVO implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Integer sort;
}
