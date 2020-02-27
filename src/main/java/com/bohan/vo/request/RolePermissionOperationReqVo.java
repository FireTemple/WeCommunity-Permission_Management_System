package com.bohan.vo.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RolePermissionOperationReqVo {


//


    @ApiModelProperty(value = "角色ID")
    private String roleId;


    @ApiModelProperty(value = "菜单角色集合")
    private List<String> permissionIds;
}
