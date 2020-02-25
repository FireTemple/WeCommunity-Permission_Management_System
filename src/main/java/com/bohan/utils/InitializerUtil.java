package com.bohan.utils;


import com.bohan.config.TokenSetting;
import org.springframework.stereotype.Component;

@Component
public class InitializerUtil {

    private TokenSetting tokenSetting;

    public InitializerUtil(TokenSetting tokenSetting) {
        JwtTokenUtil.setTokenSettings(tokenSetting);
    }
}
