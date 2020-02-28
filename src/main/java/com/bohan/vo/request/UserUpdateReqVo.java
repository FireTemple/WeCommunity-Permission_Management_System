package com.bohan.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserUpdateReqVo {

    @ApiModelProperty(value = "用户id")
    @NotBlank(message = "用户id不能为空")
    private String id;

    @ApiModelProperty(value = "账号")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "账户状态(1.正常 2.锁定 )")
    private Integer status;

    @ApiModelProperty(value = "所属部门")
    private String deptId;
}
