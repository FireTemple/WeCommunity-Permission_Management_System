package com.bohan.vo.respose;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LoginRespVo {

    @ApiModelProperty(value = "正常业务的 Token")
    private String accessToken;

    @ApiModelProperty(value = "刷新使用的 Token")
    private String refreshToken;

    @ApiModelProperty(value = "user id")
    private String id;

    @ApiModelProperty(value = "username")
    private String username;

    @ApiModelProperty(value = "user phone number")
    private String phone;
}
