package com.bohan.vo.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class LoginReqVo {


    @ApiModelProperty(value = "username")
    @NotBlank(message = "username can not be empty")
    private String username;

    @ApiModelProperty(value = "password")
    @NotNull(message = "password can not be null")
    private String password;

    @ApiModelProperty(value = "user type 1 for pc, 2 for app")
    @NotEmpty(message = "login device type can not be empty")
    private String type;

}
