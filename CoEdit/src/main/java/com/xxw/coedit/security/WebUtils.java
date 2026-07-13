package com.xxw.coedit.security;
import com.xxw.coedit.common.enums.ErrorCode;
import com.xxw.coedit.common.exceptions.BizException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class WebUtils {
    private final JwtUtil jwtUtil;

    public WebUtils(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 从请求中获取当前登录用户 ID
     */
    public Long currentUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new BizException(ErrorCode.AUTH_TOKEN_INVALID);
        }

        String token = header.substring(7);
        return jwtUtil.getUserId(token);
    }
}
