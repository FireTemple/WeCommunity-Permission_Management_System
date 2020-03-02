package com.bohan.controller;

import com.bohan.aop.annotation.MyLog;
import com.bohan.entity.SysRole;
import com.bohan.service.RoleService;
import com.bohan.utils.DataResult;
import com.bohan.vo.request.AddRoleReqVo;
import com.bohan.vo.request.RolePageReqVo;
import com.bohan.vo.request.RolePermissionOperationReqVo;
import com.bohan.vo.request.RoleUpdateReqVo;
import com.bohan.vo.respose.PageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.crypto.Data;

@RestController
@RequestMapping("/api")
@Api(tags = "组织管理-角色管理", description = "角色管理相关接口")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/roles")
    @ApiOperation(value = "分页获取角色数据")
    @MyLog(title = "组织管理-角色管理", action = "分页获取角色数据")
    public DataResult<PageVo<SysRole>> pageInfo(@RequestBody RolePageReqVo vo){
        DataResult result = DataResult.success();
        result.setData(roleService.pageInfo(vo));
        return result;
    }

    @PostMapping("/role")
    @ApiOperation(value = "新增角色接口")
    @MyLog(title = "组织管理-角色管理", action = "新增角色接口")
    public DataResult<SysRole> addRole(@RequestBody @Valid AddRoleReqVo vo){
        DataResult result = DataResult.success();
        result.setData(roleService.addRole(vo));
        return result;
    }


    @GetMapping("/role/{id}")
    @ApiOperation(value = "获取用户详情")
    @MyLog(title = "组织管理-角色管理", action = "获取用户详情")
    public DataResult detailInfo(@PathVariable("id") String id){
        DataResult result = DataResult.success();
        result.setData(roleService.detailInfo(id));
        return result;
    }

    @PutMapping("/role")
    @ApiOperation(value = "更新角色信息接口")
    @MyLog(title = "组织管理-角色管理", action = "更新角色信息接口")
    public DataResult updateRole(@RequestBody @Valid RoleUpdateReqVo vo){
        DataResult result = DataResult.success();
        roleService.updateRole(vo);
        return result;
    }

    @DeleteMapping("/role/{id}")
    @ApiOperation(value = "删除用户接口")
    @MyLog(title = "组织管理-角色管理", action = "删除用户接口")
    public DataResult deleteRole(@PathVariable("id")  String id){
        roleService.deleteRole(id);
        return DataResult.success();
    }
}
