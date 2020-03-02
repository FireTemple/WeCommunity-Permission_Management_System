package com.bohan.controller;


import com.bohan.aop.annotation.MyLog;
import com.bohan.constant.Constant;
import com.bohan.service.HomeService;
import com.bohan.utils.DataResult;
import com.bohan.utils.JwtTokenUtil;
import com.bohan.vo.respose.HomeRespVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@Api(tags = "home page", description = "首页相关模块")
public class HomeController {
    @Autowired
    private HomeService homeService;

    @GetMapping("/home")
    @ApiOperation(value = "获取首页数据")
    @MyLog(title = "home page", action = "获取首页数据")
    public DataResult<HomeRespVo> getHome(HttpServletRequest request){
        String accessToken = request.getHeader(Constant.ACCESS_TOKEN);
        String refreshToken = request.getHeader(Constant.REFRESH_TOKEN);

        String userId = JwtTokenUtil.getUserId(accessToken);
        DataResult result = DataResult.success();

        result.setData(homeService.getHome(userId));
        return result;
    }
}
