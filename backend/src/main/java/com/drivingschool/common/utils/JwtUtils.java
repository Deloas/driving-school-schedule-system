package com.drivingschool.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类 — Token 生成与解析
 * <p>
 * 使用 HMAC-SHA256 算法签名。
 * Token 中存储 userId、username、role、relatedId，
 * 后续接口从 Token 中解析用户身份而无需每次查询数据库。
 * </p>
 */
@Component
public class JwtUtils {

    /** JWT 签名密钥（至少 256 位，对应 HS256） */
    @Value("${jwt.secret:DrivingSchoolSecretKey2026ForJWTTokenSigning!!}")
    private String secret;

    /** Token 有效期（毫秒），默认 24 小时 */
    @Value("${jwt.expiration:86400000}")
    private long expiration;

    /**
     * 生成 JWT Token
     *
     * @param userId    用户ID
     * @param username  登录账号
     * @param role      角色
     * @param relatedId 关联业务ID
     * @return JWT Token 字符串
     */
    public String generateToken(Long userId, String username, String role, Long relatedId) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", role)
                .claim("relatedId", relatedId)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(key)
                .compact();
    }

    /**
     * 解析 JWT Token 中的 Claims
     *
     * @param token JWT Token 字符串
     * @return Claims 对象，解析失败返回 null
     */
    public Claims parseToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            // Token 过期、签名错误、格式错误等均返回 null
            return null;
        }
    }

    /**
     * 判断 Token 是否有效
     *
     * @param token JWT Token 字符串
     * @return true 表示 Token 存在且未过期
     */
    public boolean validateToken(String token) {
        Claims claims = parseToken(token);
        return claims != null && !claims.getExpiration().before(new Date());
    }
}
