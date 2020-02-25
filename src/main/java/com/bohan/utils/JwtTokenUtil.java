package com.bohan.utils;

import com.bohan.config.TokenSetting;
import com.bohan.constant.Constant;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.DatatypeConfigurationException;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtTokenUtil {
    private static String secretKey;
    private static Duration accessTokenExpireTime;
    private static Duration refreshTokenExpireTime;
    private static Duration refreshTokenExpireAppTime;
    private static String  issuer;

    public static void setTokenSettings(TokenSetting tokenSetting){
        secretKey = tokenSetting.getSecretKey();
        accessTokenExpireTime = tokenSetting.getAccessTokenExpireTime();
        refreshTokenExpireAppTime = tokenSetting.getRefreshTokenExpireAppTime();
        refreshTokenExpireTime = tokenSetting.getRefreshTokenExpireTime();
        issuer = tokenSetting.getIssuer();
    }

    /**
     * 获取Token
     * @param subject
     * @param claims
     * @return
     */
    public static String getAccessToken(String subject, Map<String,Object> claims){
        return generateToken(issuer,subject,claims,accessTokenExpireTime.toMillis(),secretKey);
    }

    /**
     * 刷新 PC Token
     * @param subject
     * @param claims
     * @return
     */

    public static String getRefreshToken(String subject,Map<String,Object> claims){
        return generateToken(issuer,subject,claims,refreshTokenExpireTime.toMillis(),secretKey);
    }

    /**
     * 刷新 APP Token
     * @param subject
     * @param claims
     * @return
     */
    public static String getRefreshAppToken(String subject,Map<String,Object> claims){
        return generateToken(issuer,subject,claims,refreshTokenExpireAppTime.toMillis(),secretKey);
    }

    /**
     * 生成Token
     * @param issuer
     * @param subject
     * @param claims
     * @param ttlMillis
     * @param secret
     * @return
     */


    public static String generateToken(String issuer, String subject, Map<String,Object> claims
    , long ttlMillis, String secret){
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        byte[] signingKey = DatatypeConverter.parseBase64Binary(secret);

        JwtBuilder builder = Jwts.builder();

        if(null != claims){
            builder.setClaims(claims);
        }
        if(!StringUtils.isEmpty(subject)){
            builder.setSubject(subject);
        }
        if(!StringUtils.isEmpty(issuer)){
            builder.setIssuer(issuer);
        }
        builder.setIssuedAt(now);
        if(ttlMillis >= 0){
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        builder.signWith(signatureAlgorithm, signingKey);
        return builder.compact();
    }

    //解密Token
    public static Claims getClaimsFromToken(String token) {
        Claims claims;
        try { claims =
                Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey)).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    //获取userid
    public static String getUserId(String token){
        String userId=null;
        try {
            Claims claims = getClaimsFromToken(token);
            userId = claims.getSubject(); } catch (Exception e) {
            log.error("error={}",e); }
        return userId;
    }
    // 获取User name
    public static String getUserName(String token){
        String username=null;
        try {
            Claims claims = getClaimsFromToken(token);
            username = (String) claims .get(Constant.JWT_USER_NAME); } catch (Exception e) {
            log.error("error={}",e); }
        return username;
    }

    public static Boolean validateToken(String token) {
        Claims claimsFromToken = getClaimsFromToken(token);
        return (null!=claimsFromToken && !isTokenExpired(token));
    }

    public static Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token); Date expiration = claims.getExpiration(); return expiration.before(new Date());
        } catch (Exception e) { log.error("error={}",e); return true;
        }
    }

    public static String refreshToken(String refreshToken,Map<String, Object> claims) {
        String refreshedToken;
        try {
            Claims parserclaims = getClaimsFromToken(refreshToken);
            /**
             * 刷新token的时候如果为空说明原先的 用户信息不变 所以就引用上个token里的内容
             */
            if(null==claims){
                claims=parserclaims;
            }
            refreshedToken = generateToken(parserclaims.getIssuer(),parserclaims.getSubject(),claims,accessTokenExpireTime.toMillis() ,secretKey);
        } catch (Exception e) { refreshedToken = null; log.error("error={}",e);
        }
        return refreshedToken;
    }

    public static long getRemainingTime(String token){
        long result = 0;
        try {
            long nowMillis = System.currentTimeMillis();
            result= getClaimsFromToken(token).getExpiration().getTime()-nowMillis;
        } catch (Exception e) {
            log.error("error={}",e);
        }
        return result;
    }

}
