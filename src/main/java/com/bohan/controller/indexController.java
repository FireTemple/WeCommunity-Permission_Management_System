package com.bohan.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/index")
@Api(tags = "视图",description = "跳转视图控制器")
public class indexController {

    @GetMapping("/404")
    @ApiOperation(value = "跳转404错误页面")
    public String error404(){
        return "error/404";
    }

    @GetMapping("/login")
    @ApiOperation(value = "跳转登录界面")
    public String login(){
        return "login";
    }

    @GetMapping("/home")
    @ApiOperation(value = "跳转首页页面")
    public String home(){
        return "home";
    }

    @GetMapping("/main")
    @ApiOperation(value = "跳转主页面")
    public String main(){
        return "main";
    }

    @GetMapping("/menus")
    @ApiOperation(value = "菜单权限管理入口")
    public String menus(){
        return "menus/menu";
    }

    @GetMapping("/roles")
    @ApiOperation(value = "角色权限管理入口")
    public String roles(){
        return "roles/role";
    }

}