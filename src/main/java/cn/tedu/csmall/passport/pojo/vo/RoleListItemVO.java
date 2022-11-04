package cn.tedu.csmall.passport.pojo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoleListItemVO implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Integer sort;
}
