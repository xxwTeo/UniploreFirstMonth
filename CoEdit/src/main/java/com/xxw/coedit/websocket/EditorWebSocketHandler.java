package com.xxw.coedit.websocket;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxw.coedit.dto.request.EditOperationDTO;
import com.xxw.coedit.service.FileService;
import com.xxw.coedit.service.LockService;
import com.xxw.coedit.service.OnlineUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@RequiredArgsConstructor
public class EditorWebSocketHandler implements WebSocketHandler {

    private final FileService fileService;
    private final LockService lockService;
    private final OnlineUserService onlineUserService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<Long, CopyOnWriteArraySet<WebSocketSession>> sessions =
            new ConcurrentHashMap<>();


    /**
     * WebSocket 连接建立后：
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 1. 解析 fileId / userId
        Long fileId = getFileId(session);
        Long userId = (Long) session.getAttributes().get("userId");
        // 2. 将该 Session 加入内存管理
        sessions.computeIfAbsent(fileId, k -> new CopyOnWriteArraySet<>()).add(session);
        // 3. 写入 Redis 在线状态
        onlineUserService.enterEdit(fileId, userId);
        // 4. 广播最新在线用户列表
        broadcast(fileId, Map.of(
                "类型", "用户进入",
                "用户ID", userId,
                "在线列表", onlineUserService.getEditors(fileId)
        ));
    }

    /**
     * 处理客户端发来的编辑消息
     * 流程：校验身份 → 解析操作 → 加锁 → 应用变更 → 广播 → 释放锁
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // 1. 解析 fileId / userId
        Long fileId = getFileId(session);
        Long userId = (Long) session.getAttributes().get("userId");

        // 2. 将客户端发送的 JSON 消息反序列化为编辑操作对象
        EditOperationDTO op = objectMapper.readValue(
                message.getPayload().toString(),
                EditOperationDTO.class
        );
        // 3. 获取编辑锁（防止并发修改同一文件）
        lockService.tryLock(fileId, userId);
        try {
            // 4. 将操作应用到文件，生成最新内容
            String newContent = fileService.applyOperation(fileId, op, userId);
            // 5. 广播操作结果给同一文件的所有在线用户
            broadcast(fileId, Map.of(
                    "类型", "编辑操作",
                    "操作", op,
                    "内容", newContent
            ));
        } finally {
            // 6. 无论成功与否，都必须释放锁
            lockService.releaseLock(fileId, userId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    /**
     * WebSocket 连接关闭后的清理工作
     * 流程：解析身份 → 移除 Session → 更新在线状态 → 广播离开事件
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        // 1. 解析 fileId / userId
        Long fileId = getFileId(session);
        Long userId = (Long) session.getAttributes().get("userId");
        // 2. 从内存 Session 池中移除该连接（防止内存泄漏）
        sessions.getOrDefault(fileId, new CopyOnWriteArraySet<>()).remove(session);
        // 3. 从 Redis 在线集合中移除该用户
        onlineUserService.exitEdit(fileId, userId);
        // 4. 广播“用户离开”事件，通知剩余编辑者更新在线列表
        broadcast(fileId, Map.of(
                "类型", "用户离开",
                "用户ID", userId,
                "在线列表", onlineUserService.getEditors(fileId)
        ));
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    // 从 WebSocket 连接的 URI 中解析出文件 ID（fileId）
    private Long getFileId(WebSocketSession session) {
        String path = session.getUri().getPath();
        return Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
    }

    // 向指定文件的所有在线编辑用户广播消息
    private void broadcast(Long fileId, Object msg) {
        try {
            // 1. 将消息对象序列化为 JSON
            String text = objectMapper.writeValueAsString(msg);
            // 2. 向该文件的所有在线 Session 广播
            for (WebSocketSession s : sessions.getOrDefault(fileId, new CopyOnWriteArraySet<>())) {
                // 3. 仅向仍然保持连接的 Session 发送
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(text));
                }
            }
        } catch (IOException e) { }
    }

}