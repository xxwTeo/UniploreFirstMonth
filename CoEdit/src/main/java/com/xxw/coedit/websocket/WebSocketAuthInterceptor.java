package com.xxw.coedit.websocket;

import com.xxw.coedit.common.enums.ErrorCode;
import com.xxw.coedit.common.exceptions.BizException;
import com.xxw.coedit.security.JwtUtil;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;


/**
 * WebSocket 握手鉴权拦截器
 */
@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * WebSocket 握手前的拦截方法
     *
     * @param request    HTTP 请求对象（包含 URL、Header 等信息）
     * @param response   HTTP 响应对象
     * @param wsHandler  WebSocket 处理器
     * @param attributes WebSocket Session 的属性容器（核心）
     * @return true  = 允许握手，建立 WebSocket 连接
     *         false = 拒绝握手，连接中断
     * @throws Exception 鉴权失败时抛出业务异常
     */
    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {
        // 1. 获取 URL 中的查询参数
        String query = request.getURI().getQuery();
        if (query == null || !query.contains("token=")) {
            // 未携带 token，直接拒绝连接
            throw new BizException(ErrorCode.AUTH_TOKEN_INVALID);
        }
        // 2. 解析 URL 参数，提取 token
        MultiValueMap<String, String> params
                = UriComponentsBuilder.fromUri(request.getURI())
                .build()
                .getQueryParams();
        String token = params.getFirst("token");
        if (token == null) {
            throw new BizException(ErrorCode.AUTH_TOKEN_INVALID);
        }
        try {
            // 3. 校验 JWT Token，解析用户身份
            Long userId = jwtUtil.getUserId(token);
            String username = jwtUtil.getUsername(token);
            // 4. 将用户信息存入 WebSocket Session 属性中
            attributes.put("userId", userId);
            attributes.put("username", username);
            attributes.put("sessionId", params.getFirst("sessionId"));
            // 5. 放行握手，建立 WebSocket 连接
            return true;
        } catch (Exception e) {
            throw new BizException(ErrorCode.AUTH_TOKEN_INVALID);
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {}

}