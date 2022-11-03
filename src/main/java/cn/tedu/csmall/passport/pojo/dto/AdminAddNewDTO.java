package cn.tedu.csmall.passport.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class AdminAddNewDTO implements Serializable {

    @ApiModelProperty(value = "用户名",required = true)
    @NotNull(message = "添加管理员失败,必须提交用户名!")
    private String username;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("昵称")
    private String nickname;
    @ApiModelProperty("头像路径")
    private String avatar;
    @ApiModelProperty("电话号码")
    private String phone;
    @ApiModelProperty("电子邮箱")
    private String email;
    @ApiModelProperty("描述")
    private String description;
    @ApiModelProperty("是否启用")
    private Integer enable;
}
