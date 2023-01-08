package org.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.example.model.LoginUser;

import java.util.Date;

@Slf4j
public class JWTUtil {

    /**
     * token expire in 7 days (7 * 5 for test)
     */
    private static final long EXPIRE = 1000 * 60 * 60 * 24 * 7 * 20; // 20 week for test

    /**
     * secret for encode
     */
    private static final String SECRET = "xdshopbob0902";

    /**
     * token prefix
     */
    private static final String TOKEN_PREFIX = "xdshopbob";

    /**
     * subject
     */
    private static final String SUBJECT = "xdshop";

    /**
     * generate token by user info
     * @param loginUser
     * @return
     */
    public static String geneJsonWebToken(LoginUser loginUser) {
        if (loginUser == null) {
            throw new NullPointerException("loginUser object is null");
        }

        String token = Jwts.builder().setSubject(SUBJECT)
                .claim("head_img", loginUser.getHeadImg())
                .claim("id", loginUser.getId())
                .claim("name", loginUser.getName())
                .claim("mail", loginUser.getMail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
        token = TOKEN_PREFIX + token;

        log.info("decode is:{}", checkJWT(token).toString());

        return token;
    }

    /**
     * check token method
     * @param token
     * @return
     */
    public static Claims checkJWT(String token) {
        try {
            log.info("token decode:{}", token);
            final Claims claims = Jwts.parser().setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody();
            return claims;
        } catch (Exception e) {
            log.info("jwt token decode fail");
            return null;
        }
    }

}
