package com.bohan.shiro;

import com.bohan.constant.Constant;
import com.bohan.service.RedisService;
import com.bohan.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

public class CustomRealm extends AuthorizingRealm {

//    @Autowired
//    private RedisService redisService;

    /**
     * 这里是处理用户前端过来的Token来获得信息进行验证的
     * @param token
     * @return
     */

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof CustomUsernamePasswordToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String accessToken = (String) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        Claims claims = JwtTokenUtil.getClaimsFromToken(accessToken);
        /**
         *
         * 返回该用户的授权信息
         */
        if(claims.get(Constant.JWT_ROLES_KEY) != null){
            info.addRoles((Collection<String>) claims.get(Constant.JWT_ROLES_KEY));
        }

        if(claims.get(Constant.JWT_PERMISSIONS_KEY) != null){
            info.addStringPermissions((Collection<String>) claims.get(Constant.JWT_PERMISSIONS_KEY));
        }

        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        CustomUsernamePasswordToken customUsernamePasswordToken = (CustomUsernamePasswordToken) authenticationToken;
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(customUsernamePasswordToken.getPrincipal(), customUsernamePasswordToken.getCredentials(),CustomRealm.class.getName());

        return info;
    }
}
