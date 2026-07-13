package com.xxw.coedit.security;

import com.xxw.coedit.common.enums.ErrorCode;
import com.xxw.coedit.common.exceptions.BizException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    //密钥字符串（用于 HS256 签名）
    @Value("${jwt.secret}")
    private String secret;

    //Token 有效期限：7 天
    @Value("${jwt.expiration}")
    private Long expiration;

    //将密钥字符串转换为 JJWT 要求的 SecretKey 对象
    private SecretKey key;

    /**
     * 初始化密钥（PostConstruct 保证只执行一次）
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

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
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析并验证 JWT Token
     * @param token 前端传来的 JWT 字符串
     * @return Claims 对象，包含 Token 中存储的所有数据（subject、username、过期时间等）
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            throw new BizException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new BizException(ErrorCode.TOKEN_INVALID);
        }

    }

    /**
     * 从 Token 中获取用户 ID
     */
    public Long getUserId(String token) {
        return Long.parseLong(parseToken(token).getSubject());
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsername(String token) {
        return parseToken(token).get("username", String.class);
    }
}
