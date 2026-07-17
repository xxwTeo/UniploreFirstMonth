package com.xxw.coedit.websocket;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxw.coedit.dto.request.EditOperationDTO;
import com.xxw.coedit.security.SessionAccessGate;
import com.xxw.coedit.service.EditSessionService;
import com.xxw.coedit.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
@RequiredArgsConstructor
public class EditorWebSocketHandler implements WebSocketHandler {

    private final FileService fileService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EditSessionService editSessionService;

    private final Map<Long, CopyOnWriteArraySet<WebSocketSession>> sessions =
            new ConcurrentHashMap<>();
    private final SessionAccessGate sessionAccessGate;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long fileId = getFileId(session);
        Long userId = (Long) session.getAttributes().get("userId");
        String sessionId = (String) session.getAttributes().get("sessionId");
        String username = (String) session.getAttributes().get("username");

        sessions.computeIfAbsent(fileId, k -> new CopyOnWriteArraySet<>()).add(session);
        editSessionService.createSession(fileId, userId, sessionId);

        broadcast(fileId, Map.of(
                "类型", "用户进入",
                "用户名", username,
                "在线列表", editSessionService.listByFiledId(fileId)
        ));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        Long fileId = getFileId(session);
        Long userId = (Long) session.getAttributes().get("userId");

        String payload = message.getPayload().toString();
        Map<String, Object> raw = objectMapper.readValue(payload, Map.class);
        String type = (String) raw.get("op");

        if ("heartbeat".equals(type)) {
            sessionAccessGate.ensureEditable(fileId, userId, (String) raw.get("sessionId"));
            return;
        }

        sessionAccessGate.ensureEditable(fileId, userId, (String) raw.get("sessionId"));

        EditOperationDTO op = objectMapper.convertValue(raw, EditOperationDTO.class);

        String newContent = fileService.applyOperation(fileId, op, userId);
        broadcast(fileId, Map.of(
                "类型", "编辑操作",
                "操作", op,
                "内容", newContent
        ));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        Long fileId = getFileId(session);
        Long userId = (Long) session.getAttributes().get("userId");
        String sessionId = (String) session.getAttributes().get("sessionId");
        String username = (String) session.getAttributes().get("username");

        sessions.getOrDefault(fileId, new CopyOnWriteArraySet<>()).remove(session);
        editSessionService.closeSession(fileId, userId, sessionId);

        broadcast(fileId, Map.of(
                "类型", "用户离开",
                "用户名", username,
                "在线列表", editSessionService.listByFiledId(fileId)
        ));
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private Long getFileId(WebSocketSession session) {
        String path = session.getUri().getPath();
        return Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
    }

    private void broadcast(Long fileId, Object msg) {
        try {
            String text = objectMapper.writeValueAsString(msg);
            for (WebSocketSession s : sessions.getOrDefault(fileId, new CopyOnWriteArraySet<>())) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(text));
                }
            }
        } catch (IOException e) {
            log.warn("广播消息失败: {}", e.getMessage());
        }
    }
}