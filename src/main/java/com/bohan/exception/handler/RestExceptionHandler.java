package com.bohan.exception.handler;


import com.bohan.exception.BusinessException;
import com.bohan.exception.code.BaseResponseCode;
import com.bohan.utils.DataResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(Exception.class)
    public DataResult exception(Exception e){
        log.error("Exception,{},{}", e.getLocalizedMessage(),e);
        return DataResult.getResult(BaseResponseCode.SYSTEM_ERROR);
    }
    //处理我们写的业务逻辑错误
    @ExceptionHandler(BusinessException.class)
    public DataResult businessException(BusinessException e){
        log.error("BusinessException,{}",e.getDefaultMessage());
        return new DataResult(e.getCode(),e.getDefaultMessage());
    }
    // 处理数据验证错误
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public DataResult methodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error("MethodArgumentNotValidException bindingResult.allErrors():{},exception:{}", e.getBindingResult(),e.getMessage());
        //获取所有错误
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        return createValidExceptionResp(errors);

    }

    @ExceptionHandler(UnauthenticatedException.class)
    public DataResult unauthenticatedException(UnauthenticatedException e){
        log.error("UnauthenticatedException ,{},{}", e.getLocalizedMessage(), e);
        return DataResult.getResult(BaseResponseCode.NOT_PERMISSION,"没有权限");

    }

    private DataResult createValidExceptionResp(List<ObjectError> errors){
        String[] msgs = new String[errors.size()];
        int i = 0;
        for(ObjectError objectError : errors){
            msgs[i] = objectError.getDefaultMessage();
            log.info("msg:{}",msgs[i]);
            i ++;
        }
        return new DataResult(BaseResponseCode.METHOD_IDENTITY_ERROR.getCode(), msgs[0]);
    }
}
