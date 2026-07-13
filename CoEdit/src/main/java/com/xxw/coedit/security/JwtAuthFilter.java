package com.xxw.coedit.security;

import io.jsonwebtoken.Claims;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxw.coedit.common.result.Result;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;


@Slf4j
public class JwtAuthFilter implements Filter {
    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    /**
     * 由 SecurityConfig @Bean 工厂方法调用，非 @Component 自动注册
     */
    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();

        // 1. 注册 & 登录直接放行
        if (uri.startsWith("/api/auth/register") ||
                uri.startsWith("/api/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 获取 Token
        String header = request.getHeader(HEADER);
        if (header == null || !header.startsWith(PREFIX)) {
            writeUnauthorized(response, "未登录，请先登录");
            return;
        }

        String token = header.substring(PREFIX.length());

        // 3. 校验 Token，并向 SecurityContext 写入认证身份
        try {
            Claims claims = jwtUtil.parseToken(token);

            Long userId = Long.parseLong(claims.getSubject());
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, List.of());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.warn("Token 校验失败: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            writeUnauthorized(response, "Token 非法或已过期");
            return;
        }

        // 4. Token 合法，放行
        filterChain.doFilter(request, response);
    }

    private void writeUnauthorized(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        Result<?> result = Result.fail(401, msg);
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }
}