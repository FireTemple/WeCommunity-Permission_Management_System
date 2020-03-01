package com.bohan.controller;

import com.bohan.constant.Constant;
import com.bohan.entity.SysUser;
import com.bohan.exception.code.BaseResponseCode;
import com.bohan.mapper.SysDeptMapper;
import com.bohan.service.RedisService;
import com.bohan.service.UserService;
import com.bohan.service.impl.UserServiceImpl;
import com.bohan.utils.DataResult;
import com.bohan.utils.JwtTokenUtil;
import com.bohan.vo.request.*;
import com.bohan.vo.respose.LoginRespVo;
import com.bohan.vo.respose.PageVo;
import com.bohan.vo.respose.UserOwnRoleRespVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@Api(tags = "user 相关接口")
@Slf4j
public class UserController {


    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SysDeptMapper sysDeptMapper;

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

    @PostMapping("/user")
    @ApiOperation(value = "新增用户")
    public DataResult ADDuSER(@RequestBody @Valid UserAddReqVo vo){
        DataResult result = DataResult.success();
        userService.addUser(vo);
        return result;
    }

    @GetMapping("/user/roles/{userId}")
    @ApiOperation(value = "用户拥有的角色数据接口")
    public DataResult<UserOwnRoleRespVo> getUserOwnRole(@PathVariable("userId") String userId){
        DataResult result = DataResult.success();
        result.setData(userService.getUserOwnRole(userId));
        return result;
    }

    @PutMapping("/user/roles")
    @ApiOperation(value = "保持用户拥有的角色信息接口")
    public DataResult saveUserOwnRole(@RequestBody @Valid UserOwnRoleReqVo vo){
        DataResult result = DataResult.success();
        userService.setUserOwnRole(vo);
        return result;
    }

    @GetMapping("/user/token")
    @ApiOperation(value = "Jwt token 刷新接口")
    public DataResult<String> refreshToken(HttpServletRequest request){
        String refreshToken = request.getHeader(Constant.REFRESH_TOKEN);
        String newAccessToken = userService.refreshToken(refreshToken);
        DataResult result = DataResult.success();
        result.setData(newAccessToken);
        return result;
    }

    @PutMapping("/user")
    @ApiOperation(value = "更新用户接口")
    public DataResult updateUserInfo(@RequestBody @Valid UserUpdateReqVo vo, HttpServletRequest request){
        String accessToken = request.getHeader(Constant.ACCESS_TOKEN);
        String curUserId = JwtTokenUtil.getUserId(accessToken);
        userService.updateUserInfo(vo,curUserId);
        DataResult result = DataResult.success();
        return result;
    }

    @DeleteMapping("user")
    @ApiOperation(value = "批量删除用户接口")
    public DataResult deleteUsers(@RequestBody @ApiParam(value = "用户Id集合") List<String> list, HttpServletRequest request){
        String accessToken = request.getHeader(Constant.ACCESS_TOKEN);
        String curUserId = JwtTokenUtil.getUserId(accessToken);
        userService.deleteUsers(list, curUserId);
        DataResult result = DataResult.success();
        return result;
    }



}
