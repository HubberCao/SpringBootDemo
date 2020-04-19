package com.sp.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

/**
 * Created by admin on 2020/2/16.
 */
@ConfigurationProperties(prefix = "config.jwt")
@Component
@Data
@Slf4j
public class JWTConfig {

    private String secret;
    private long expire;
    private String header;

    /**
     * 根据身份ID获取token
     * @param uid
     * @return
     */
    public String getAccessToken(String uid) {
        Date nowDate = new Date();
        // 过期时间
        Date expireDate = new Date(nowDate.getTime() + expire * 1000);
        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(uid)
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
        return token;
    }

    /**
     * 获取 token 中的注册信息
     * @param token
     * @return
     */
    public Claims getTokenClaim(String token) {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            log.error("token过期", e);
            return null;
        }
    }

    public boolean isTokenExpired(Date expireDate) {
        return expireDate.before(new Date());
    }

    public static void main(String[] args) {
        Key KEY = new SecretKeySpec("javastack".getBytes(),
                SignatureAlgorithm.HS512.getJcaName());
        System.out.println(new String(KEY.getEncoded()));
    }
}
