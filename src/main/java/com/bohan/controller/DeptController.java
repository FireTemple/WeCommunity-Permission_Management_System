package com.bohan.controller;


import com.bohan.aop.annotation.MyLog;
import com.bohan.entity.SysDept;
import com.bohan.service.impl.DeptServiceImpl;
import com.bohan.utils.DataResult;
import com.bohan.vo.request.DeptAddReqVo;
import com.bohan.vo.request.DeptUpdateReqVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Api(tags = "组织部门-部门管理",description = "部门管理相关接口")
@RequestMapping("/api")
public class DeptController {

    @Autowired
    private DeptServiceImpl deptService;

    @GetMapping("/depts")
    @ApiOperation(value = "查询所有部门接口")
    @MyLog(title = "组织部门-部门管理", action = "查询所有部门接口")
    public DataResult<List<SysDept>> getAllDepts(){
        DataResult result = DataResult.success();
        result.setData(deptService.selectAll());
        return result;
    }

    @GetMapping("/dept/tree")
    @ApiOperation(value = "部门树型结构列表接口")
    @MyLog(title = "组织部门-部门管理", action = "部门树型结构列表接口")
    public DataResult<List<SysDept>> getDeptTree(@RequestParam(required = false) String deptId){
        DataResult result = DataResult.success();
        result.setData(deptService.deptTreeList(deptId));
        return result;
    }

    @PostMapping("/dept")
    @ApiOperation(value = "新增部门数据接口")
    @MyLog(title = "组织部门-部门管理", action = "新增部门数据接口")
    public DataResult<SysDept> addDept(@RequestBody @Valid DeptAddReqVo vo){
        DataResult result = DataResult.success();
        result.setData(deptService.addDept(vo));
        return result;
    }

    @PutMapping("/dept")
    @ApiOperation(value = "更新部门数据接口")
    @MyLog(title = "组织部门-部门管理", action = "更新部门数据接口")
    public DataResult updateDept(@RequestBody @Valid DeptUpdateReqVo vo){
        deptService.updateDept(vo);
        return DataResult.success();
    }

    @DeleteMapping("/dept/{id}")
    @ApiOperation(value = "删除部门数据接口")
    @MyLog(title = "组织部门-部门管理", action = "删除部门数据接口")
    public DataResult deleteDept(@PathVariable("id") String id){
        deptService.deleteDept(id);
        return DataResult.success();
    }
}
