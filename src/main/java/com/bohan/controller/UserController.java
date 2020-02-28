package com.bohan.controller;

import com.bohan.constant.Constant;
import com.bohan.entity.SysUser;
import com.bohan.exception.code.BaseResponseCode;
import com.bohan.service.UserService;
import com.bohan.service.impl.UserServiceImpl;
import com.bohan.utils.DataResult;
import com.bohan.vo.request.LoginReqVo;
import com.bohan.vo.request.UserPageReqVO;
import com.bohan.vo.respose.LoginRespVo;
import com.bohan.vo.respose.PageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@Api(tags = "user 相关接口")
@Slf4j
public class UserController {


    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/user/login")
    @ApiOperation(value = "登录接口")
    public DataResult<LoginRespVo> login(@RequestBody @Valid LoginReqVo vo){
        DataResult result = DataResult.success();
        System.out.println("userService" + userService);
        result.setData(userService.login(vo));
        return result;
    }


    @GetMapping("/user/logout")
    @ApiOperation(value = "用户登出接口")
    public DataResult logout(HttpServletRequest request){
        String accessToken = null;
        String refreshToken = null;
        try {
            accessToken = request.getHeader(Constant.ACCESS_TOKEN);
            refreshToken = request.getHeader(Constant.REFRESH_TOKEN);
            userService.logout(accessToken, refreshToken);

        } catch (Exception e) {
            log.error("logout error {}", e);
        }
        return DataResult.success();
    }


    @GetMapping("/user/unLogin")
    @ApiOperation(value = "引导客户端去登录")
    public DataResult unLogin(){
        DataResult result= DataResult.getResult(BaseResponseCode.TOKEN_ERROR);
        return result;
    }

    @PostMapping("/users")
    @ApiOperation(value = "分页获取用户列表接口")
//    @RequiresPermissions("sys:user:list")
    public DataResult<PageVo<SysUser>> pageInfo(@RequestBody UserPageReqVO vo){
        DataResult<PageVo<SysUser>> result = DataResult.success();
        result.setData(userService.pageInfo(vo));
        return result;
    }
}
