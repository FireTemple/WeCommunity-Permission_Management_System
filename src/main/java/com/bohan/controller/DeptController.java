package com.bohan.controller;


import com.bohan.entity.SysDept;
import com.bohan.service.impl.DeptServiceImpl;
import com.bohan.utils.DataResult;
import com.bohan.vo.request.DeptAddReqVo;
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
    public DataResult<List<SysDept>> getAllDepts(){
        DataResult result = DataResult.success();
        result.setData(deptService.selectAll());
        return result;
    }

    @GetMapping("/dept/tree")
    @ApiOperation(value = "查询所有部门接口")
    public DataResult<List<SysDept>> getDeptTree(){
        DataResult result = DataResult.success();
        result.setData(deptService.deptTreeList());
        return result;
    }

    @PostMapping("/dept")
    @ApiOperation(value = "新增部门数据接口")
    public DataResult<SysDept> addDept(@RequestBody @Valid DeptAddReqVo vo){
        DataResult result = DataResult.success();
        result.setData(deptService.addDept(vo));
        return result;
    }

}
