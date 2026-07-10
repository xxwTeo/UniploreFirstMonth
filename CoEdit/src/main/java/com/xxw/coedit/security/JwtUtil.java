package com.xxw.coedit.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    //密钥字符串（用于 HS256 签名）
    private static final String SECRET = "FileCollabSecretKey2026FileCollabSecretKey2026";
    //将密钥字符串转换为 JJWT 要求的 SecretKey 对象
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    //Token 有效期限：7 天
    private static final Long EXPIRATION = 7 * 24 * 3600 * 1000L;

    /**
     * 生成 JWT Token
     * @param userId   用户ID，存入 Token 的 subject 字段
     * @param username 用户名，作为自定义 claim 存入
     * @return 生成的 JWT 字符串
     */
    public String generateToken(Long userId, String username) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("username", username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析并验证 JWT Token
     * @param token 前端传来的 JWT 字符串
     * @return Claims 对象，包含 Token 中存储的所有数据（subject、username、过期时间等）
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从 Token 中提取用户 ID
     * @param token 前端传来的 JWT 字符串
     * @return 用户 ID（Long 类型）
     */
    public Long getUserId(String token) {
        return Long.parseLong(parseToken(token).getSubject());
    }
}
