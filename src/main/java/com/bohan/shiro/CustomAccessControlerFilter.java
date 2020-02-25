package com.bohan.shiro;

import com.alibaba.fastjson.JSON;
import com.bohan.constant.Constant;
import com.bohan.exception.BusinessException;
import com.bohan.exception.code.BaseResponseCode;
import com.bohan.utils.DataResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @ClassName: CustomAccessControlerFilter
 * TODO:类文件简单描述
 * @Author: 小霍
 * @UpdateUser: 小霍
 * @Version: 0.0.1
 */
@Slf4j
public class CustomAccessControlerFilter extends AccessControlFilter {
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        log.info(request.getMethod());
        log.info(request.getRequestURL().toString());
        //判断客户端是否携带accessToken  这里用try catch 是因为全局控制只有controller 不包括filter
        try {
            // 从Header中获取accessToken
            String accessToken = request.getHeader(Constant.ACCESS_TOKEN);
            if(StringUtils.isEmpty(accessToken)){
                throw new BusinessException(BaseResponseCode.TOKEN_NOT_NULL);
            }
            //强转 accessToken 就是 username password Token
            CustomUsernamePasswordToken customUsernamePasswordToken = new CustomUsernamePasswordToken(accessToken);
            //拿到当前线程对象 用这个Token 进行登录
            getSubject(servletRequest,servletResponse).login(customUsernamePasswordToken);

        } catch (BusinessException e) {
            customRsponse(e.getCode(),e.getDefaultMessage(),servletResponse);
            return false;
        } catch (AuthenticationException e) {
            if(e.getCause() instanceof BusinessException){
                BusinessException exception= (BusinessException) e.getCause();
                customRsponse(exception.getCode(),exception.getDefaultMessage(),servletResponse);
            }else {
                customRsponse(BaseResponseCode.SHIRO_AUTHENTICATION_ERROR.getCode(),BaseResponseCode.SHIRO_AUTHENTICATION_ERROR.getMsg(),servletResponse);
            }
            return false;
        }
        return true;
    }

    /**
     * 自定义错误响应
     * @Author:      小霍
     * @UpdateUser:
     * @Version:     0.0.1
     * @param code
     * @param msg
     * @param response
     * @return       void
     * @throws
     */
    private void customRsponse(int code, String msg, ServletResponse response){
        // 自定义异常的类，用户返回给客户端相应的JSON格式的信息
        try {
            DataResult result=DataResult.getResult(code,msg);
            response.setContentType("application/json; charset=utf-8");
            response.setCharacterEncoding("UTF-8");

            String userJson = JSON.toJSONString(result);
            OutputStream out = response.getOutputStream();
            out.write(userJson.getBytes("UTF-8"));
            out.flush();
        } catch (IOException e) {
            log.error("eror={}",e);
        }
    }
}
