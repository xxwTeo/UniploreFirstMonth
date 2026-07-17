package com.xxw.coedit.security;
import com.xxw.coedit.common.enums.ErrorCode;
import com.xxw.coedit.common.exceptions.BizException;
import com.xxw.coedit.entity.EditSession;
import com.xxw.coedit.mapper.EditSessionMapper;
import com.xxw.coedit.service.EditSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * 协同编辑会话访问门控
 */
@Component
@RequiredArgsConstructor
public class SessionAccessGate {

    private final EditSessionService editSessionService;
    private final EditSessionMapper editSessionMapper;

    /**
     * 校验当前用户是否有权限编辑该文件
     *
     * @param fileId    文件ID
     * @param userId    用户ID
     * @param sessionId 协同编辑会话ID（非协同模式下可为空）
     */
    public void ensureEditable(Long fileId, Long userId, String sessionId) {
        // 1. 查询这个文件当前是否有人在协同编辑
        int countByFileId = editSessionService.countByFileId(fileId);
        // 2. 无人协同 → 普通编辑模式，放行
        if (countByFileId == 0) return;
        // 3. 有人协同 → 必须持有有效会话
        if (sessionId == null || sessionId.isBlank()) {
            throw new BizException(ErrorCode.FILE_IN_CO_EDIT);
        }
        // 4. 查询用户所拥有session
        EditSession session = editSessionMapper.selectByFileUserSession(fileId, userId, sessionId);
        if (sessionId == null) {
            throw new BizException(ErrorCode.CO_EDIT_SESSION_EXPIRED);
        }
        // 5. 更新数据库
        session.setLastHeartbeat(LocalDateTime.now());
        editSessionService.updateById(session);
    }
}