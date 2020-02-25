package com.bohan.controller;


import com.bohan.exception.BusinessException;
import com.bohan.exception.code.BaseResponseCode;
import com.bohan.service.RedisService;
import com.bohan.utils.DataResult;
import com.bohan.vo.request.TestVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Api(tags = "测试接口")
@RequestMapping("/test")
public class TestController {
    @Autowired
    private RedisService redisService;

    @GetMapping("/index")
    @ApiOperation(value = "引导页接口")
    public String testResult(){
        return "Hello World";
    }

    @GetMapping("/home")
    @ApiOperation(value = "测试DataResult接口")
    public DataResult<String> getHome(){
        DataResult<String> dataResult = DataResult.success("success!!");
        return dataResult;
    }

    @GetMapping("/error")
    @ApiOperation(value = "测试business error 是否被正确拦截")
    public DataResult<String> TestBusinessError(@RequestParam String data){
        if(data.equals("1") || data.equals("2") || data.equals("3")){
            DataResult<String> dataResult = DataResult.getResult(0,"success",data);
            return dataResult;
        }else{
            throw  new BusinessException(BaseResponseCode.DATA_ERROR);
        }
    }


    // 这里已经要添加 valid注解 否则不开启
    @PostMapping("/validation")
    @ApiOperation(value = "测试数据的validation tool")
    public DataResult<Object>  TestValidationData(@RequestBody @Valid TestVo testVo){
        DataResult result = DataResult.success();
        return result;
    }

    @GetMapping("/redis")
    @ApiOperation(value = "测试 redis")
    public DataResult<Object> TestRedis(){
        redisService.set("springbootController", "success!");
        DataResult result = DataResult.success();
        return result;
    }
}
