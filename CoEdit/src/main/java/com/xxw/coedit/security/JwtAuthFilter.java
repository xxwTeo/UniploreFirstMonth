package com.xxw.coedit.security;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
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

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String header = request.getHeader(HEADER);

        // 解析 Bearer Token
        if (header != null && header.startsWith(PREFIX)) {
            String token = header.substring(PREFIX.length());
            try {
                Long userId = jwtUtil.getUserId(token);
                var auth = new UsernamePasswordAuthenticationToken(
                        userId, null, List.of()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                log.warn("Token 解析失败: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        // 放行，由 SecurityConfig 决定最终是否允许访问
        filterChain.doFilter(request, servletResponse);
    }
}