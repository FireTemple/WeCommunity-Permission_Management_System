package com.bohan.controller;

import com.bohan.entity.SysRole;
import com.bohan.service.RoleService;
import com.bohan.utils.DataResult;
import com.bohan.vo.request.AddRoleReqVo;
import com.bohan.vo.request.RolePageReqVo;
import com.bohan.vo.request.RolePermissionOperationReqVo;
import com.bohan.vo.respose.PageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.xml.crypto.Data;

@RestController
@RequestMapping("/api")
@Api(tags = "组织管理-橘色管理", description = "角色管理相关接口")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/roles")
    @ApiOperation(value = "分页获取角色数据")
    public DataResult<PageVo<SysRole>> pageInfo(@RequestBody RolePageReqVo vo){
        DataResult result = DataResult.success();
        result.setData(roleService.pageInfo(vo));
        return result;
    }

    @PostMapping("/role")
    @ApiOperation(value = "新增角色接口")
    public DataResult<SysRole> addRole(@RequestBody @Valid AddRoleReqVo vo){
        DataResult result = DataResult.success();
        result.setData(roleService.addRole(vo));
        return result;
    }
}
