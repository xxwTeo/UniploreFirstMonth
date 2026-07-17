package com.xxw.coedit.config;

import com.xxw.coedit.websocket.EditorWebSocketHandler;
import com.xxw.coedit.websocket.WebSocketAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final EditorWebSocketHandler editorWebSocketHandler;
    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    /**
     * 注册 WebSocket 处理器
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                // 编辑协同主入口，{file} 为文件ID
                .addHandler(editorWebSocketHandler, "ws/editor/{file}")

                // 前置拦截器：完成用户身份认证、提取 fileId / userId / sessionId
                .addInterceptors(webSocketAuthInterceptor)

                // 允许所有跨域（生产环境建议改为具体域名）
                .setAllowedOrigins("*");
    }
}