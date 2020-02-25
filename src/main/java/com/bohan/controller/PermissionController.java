package com.bohan.controller;


import com.bohan.entity.SysPermission;
import com.bohan.service.PermissionService;
import com.bohan.utils.DataResult;
import com.bohan.vo.request.PermissionAddReqVO;
import com.bohan.vo.respose.PermissionRespNodeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@Api(tags = "组织管理-菜单权限管理",description = "菜单权限管理")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;


    @GetMapping("/permissions")
    @ApiOperation(value = "获取菜单所有权限数据")
    public DataResult<List<SysPermission>> getAllPermission(){
        DataResult result = DataResult.success();
        result.setData(permissionService.selectAll());
        return result;
    }

    @GetMapping("/permission/tree")
    @ApiOperation(value = "菜单权限树接口-只递归查询到菜单接口")
    public DataResult<List<PermissionRespNodeVo>> getAllPermissionTreeExBtn(){
        DataResult result = DataResult.success();
        result.setData(permissionService.selectAllMenuByTree());
        return result;
    }

    @PostMapping("/permission")
    @ApiOperation(value = "新增菜单权限接口")
    public DataResult<SysPermission> addPermission(@RequestBody @Valid PermissionAddReqVO vo){
        DataResult result = DataResult.success();
        result.setData(permissionService.addPermission(vo));
        return result;
    }
}
