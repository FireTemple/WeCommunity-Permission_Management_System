package com.bohan.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserUpdatePwdReqVo {
    @ApiModelProperty(value = "旧密码")
    @NotBlank(message = "旧密码不能为空")
    private String oldPwd;

    @ApiModelProperty(value = "新密码")
    @NotBlank(message = "新密码不能为空")
    private String newPwd;
}
