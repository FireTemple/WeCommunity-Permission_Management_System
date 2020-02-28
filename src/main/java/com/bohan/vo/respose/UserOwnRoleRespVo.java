package com.bohan.vo.respose;

import com.bohan.entity.SysRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: UserOwnRoleRespVo
 * TODO:类文件简单描述
 * @Author: 小霍
 * @UpdateUser: 小霍
 * @Version: 0.0.1
 */
@Data
public class UserOwnRoleRespVo {

    @ApiModelProperty(value = "拥有角色集合")
    private List<String> ownRoles;

    @ApiModelProperty(value = "所有角色列表")
    private List<SysRole> allRole;
}
