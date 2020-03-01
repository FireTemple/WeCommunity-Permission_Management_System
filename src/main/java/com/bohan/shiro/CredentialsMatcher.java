package com.bohan.shiro;

import com.bohan.constant.Constant;
import com.bohan.exception.BusinessException;
import com.bohan.exception.code.BaseResponseCode;
import com.bohan.service.RedisService;
import com.bohan.utils.JwtTokenUtil;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

public class CredentialsMatcher extends HashedCredentialsMatcher {

    @Autowired
    private RedisService redisService;

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {

        /**
         * 下面这三部 通过传入的 accessToken 来拿到用户Id
         */
        CustomUsernamePasswordToken customUsernamePasswordToken = (CustomUsernamePasswordToken) token;
        String accessToken = (String) customUsernamePasswordToken.getPrincipal();
        String userId = JwtTokenUtil.getUserId(accessToken);

        /**
         * 1.Lock 2.delete 3.是否已经logout 4.是否是正确的Token
         */
        if(redisService.hasKey(Constant.ACCOUNT_LOCK_KEY + userId)){
            throw new BusinessException(BaseResponseCode.ACCOUNT_LOCK);
        }

        if(redisService.hasKey(Constant.DELETED_USER_KEY + userId)){
            throw new BusinessException(BaseResponseCode.ACCOUNT_HAS_DELETED_ERROR);
        }

        if(redisService.hasKey(Constant.JWT_ACCESS_TOKEN_BLACKLIST + accessToken)){
            throw new BusinessException(BaseResponseCode.TOKEN_ERROR);
        }

        if(!JwtTokenUtil.validateToken(accessToken)){
            throw new BusinessException(BaseResponseCode.TOKEN_PAST_DUE);
        }

        /**
         * 下面这个逻辑验证有点绕...还没搞明白
         */
        if(redisService.hasKey(Constant.JWT_REFRESH_KEY+userId)){
        /**
         * 通过剩余的过期时间比较如果token的剩余过期时间大与标记key的剩余过期时间
         * 就说明这个tokne是在这个标记key之后生成的
         */
        if(redisService.getExpire(Constant.JWT_REFRESH_KEY+userId, TimeUnit.MILLISECONDS)>JwtTokenUtil.getRemainingTime(accessToken)){
            throw new BusinessException(BaseResponseCode.TOKEN_PAST_DUE);
        }
    }
        return true;
    }
}
