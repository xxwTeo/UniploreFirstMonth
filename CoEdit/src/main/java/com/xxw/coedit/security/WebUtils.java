package com.xxw.coedit.security;
import com.xxw.coedit.common.exceptions.BizException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
@Component
public class WebUtils {
    private final JwtUtil jwtUtil;
    public WebUtils(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    public Long currentUserId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new BizException(401, "未登录");
        }
        return jwtUtil.getUserId(auth.substring(7));
    }
}