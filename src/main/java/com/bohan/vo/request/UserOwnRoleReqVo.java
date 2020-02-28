package com.bohan.vo.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class UserOwnRoleReqVo {

    @ApiModelProperty(value = "用户id")
    @NotBlank(message = "userid can not be empty")
    private String userId;

    @ApiModelProperty("赋予用户的角色id集合")
    private List<String> roleIds;
}
