package com.example.myserver.Jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
@Slf4j
@Component

public class JwtProdce {

    public void JWTTest()  {
        long currentTimeMillis = System.currentTimeMillis();
        Date issuedDate = new Date(currentTimeMillis); // 签发日期 = 当前时间
        Date expireDate = new Date(currentTimeMillis + (1000 * 5)); // 到期日期 = 当前时间 + 5s
        //设置一个jwt令牌
        String jwt = Jwts.builder()
                .setId("666") // 设置id
                .setSubject("主题") // 设置主题
                .claim("parameter","1") // 设置自定义参数
                .setIssuedAt(issuedDate) // 设置签发时间
                .setExpiration(expireDate) // 设置过期时间
                .signWith(SignatureAlgorithm.HS256, "zbinyds") // 设置JWT秘钥（非常重要，不能泄露）
                .compact();
        log.info("生成后的JWT令牌为：" + jwt);
//        Thread.sleep(10 * 1000); // 休眠10s
        // 将生成的jwt令牌进行解码
        Claims claims = Jwts.parser().setSigningKey("zbinyds").parseClaimsJws(jwt).getBody();
        log.info("解码后的JWT令牌信息：" + claims);
    }




}
