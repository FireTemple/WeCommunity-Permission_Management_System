package com.bohan.vo.respose;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserInfoRespVo {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "account")
    private String username;

    @ApiModelProperty(value = "department id")
    private String depId;

    @ApiModelProperty(value = "department name")
    private String deptName;
}
