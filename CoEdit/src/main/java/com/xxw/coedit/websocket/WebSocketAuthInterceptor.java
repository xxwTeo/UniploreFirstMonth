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
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.swing.*;
import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * WebSocket 握手前的身份认证拦截器
     *
     * @param request   HTTP 握手请求（可获取 URL / QueryString）
     * @param response  HTTP 握手响应
     * @param wsHandler WebSocket 处理器
     * @param attributes WebSocket Session 属性容器（连接建立后仍可访问）
     * @return true  = 放行，允许建立 WebSocket 连接
     *         false = 拒绝握手，连接中断
     * @throws BizException 认证失败时抛出业务异常
     */
    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        // 1️. 获取原始 QueryString（用于快速失败判断）
        String query = request.getURI().getQuery();
        if (query == null || !query.contains("token=")) {
            throw new BizException(ErrorCode.AUTH_TOKEN_INVALID);
        }
        // 2️. 使用 Spring 官方工具解析 QueryString
        MultiValueMap<String, String> params
                = UriComponentsBuilder.fromUri(request.getURI())
                .build()
                .getQueryParams();
        // 3️. 提取 Token（只取第一个值）
        String token = params.getFirst("token");
        if (token == null) {
            throw new BizException(ErrorCode.AUTH_TOKEN_INVALID);
        }
        try {
            // 4️. 校验 JWT 并解析用户 ID
            Long userId = jwtUtil.getUserId(token);
            // 5️. 将用户身份信息存入 WebSocket Session
            attributes.put("userId", userId);
            // 6️. 放行握手，允许建立 WebSocket 连接
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
            Exception exception) {

    }
}