package com.bohan.vo.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class AddRoleReqVo {

    @ApiModelProperty("角色名称")
    @NotBlank(message = "name can not be empty")
    private String name;


    @ApiModelProperty("角色描述1 = 正常，0 = 禁用")
    private String description;

    @ApiModelProperty("角色状态")
    private Integer status;

    @ApiModelProperty("角色拥有的权限ID")
    private List<String> permissions;
}
