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

    /**
     * 从当前 HTTP 请求中获取登录用户 ID
     * @param request HTTP 请求对象
     * @return 当前登录用户的 ID
     * @throws BizException 当未登录或 Token 非法时抛出（code = 401）
     */
    public Long currentUserId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new BizException(401, "未登录");
        }
        return jwtUtil.getUserId(auth.substring(7));
    }
}
