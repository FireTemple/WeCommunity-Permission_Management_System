package com.bohan.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

public class CustomUsernamePasswordToken extends UsernamePasswordToken {

    private String token;

    public CustomUsernamePasswordToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }
}
