package com.bohan.controller;


import com.bohan.aop.annotation.MyLog;
import com.bohan.entity.SysLog;
import com.bohan.service.impl.LogServiceImpl;
import com.bohan.utils.DataResult;
import com.bohan.vo.request.SysLogPageReqVo;
import com.bohan.vo.respose.PageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Api(tags = "系统管理-日志管理",description = "日志管理相关接口")
public class LogController {

    @Autowired
    private LogServiceImpl logService;

    @PostMapping("/logs")
    @ApiOperation(value = "分页查找操作日志接口")
    @RequiresPermissions("sys:log:list")
    public DataResult<PageVo<SysLog>> pageInfo(@RequestBody SysLogPageReqVo vo){
        PageVo<SysLog> sysLogPageVo = logService.pageInfo(vo);
        DataResult result = DataResult.success();
        result.setData(sysLogPageVo);
        return result;
    }

    @DeleteMapping("/log")
    @ApiOperation(value = "删除日志接口")
    @MyLog(title = "系统管理-日志管理", action = "删除日志接口")
    @RequiresPermissions("sys:log:delete")
    public DataResult deleteLog(@RequestBody @ApiParam(value = "删除的日志ID集合") List<String> logIds){
        DataResult result = DataResult.success();
        logService.deleteLog(logIds);
        return result;
    }
}
