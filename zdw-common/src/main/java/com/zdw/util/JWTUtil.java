package com.zdw.util;

import com.zdw.model.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @Author: 曾德威
 * @Date: 2023/4/30
 * @Description: 欢迎访问我的个人博客:javazdw.top
 */
@Slf4j
public class JWTUtil {
    /**
     * token过期实践 正常是7天，方便我们测试改为70
     */
    private static final long EXPIRE = 1000 * 60 * 60 * 24 * 7 * 10;
    /**
     * 加密的秘钥 很重要
     */
    private static final String SECRET = "javazdw.top";
    /**
     * 令牌前缀
     */
    private static final String TOKEN_PREFIX = "ZDW0503shop";
    /**
     * subject
     */
    private static final String SUBJECT = "zdw";

    /**
     * 根据用户信息，生成令牌
     *
     * @param loginUser
     * @return
     */
    public static String geneJsonWebToken(LoginUser loginUser) {
        if (loginUser == null) {
            throw new NullPointerException("loginUser对象为空");
        }

        Long id = loginUser.getId();
        String token = Jwts.builder().setSubject(SUBJECT)
               .claim("head_img",loginUser.getHeadImg())
               .claim("id",loginUser.getId())
               .claim("name",loginUser.getName())
               .claim("mail",loginUser.getMail())
               .setExpiration(new Date())
               .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
               .signWith(SignatureAlgorithm.HS256, SECRET).compact();
        token = TOKEN_PREFIX + token;
        return token;
    }

    /**
     * 校验token的方法
     *
     * @param token
     * @return
     */
    public static Claims checkJWT(String token) {

        try {

            final Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    // 去掉前缀
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody();

            return claims;

        } catch (Exception e) {
            log.info("jwt token解密失败");
            return null;
        }

    }
}









