package com.xxw.coedit.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxw.coedit.entity.EditSession;
import com.xxw.coedit.mapper.EditSessionMapper;
import com.xxw.coedit.mapper.UserMapper;
import com.xxw.coedit.service.EditSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class EditSessionServiceImpl extends ServiceImpl<EditSessionMapper, EditSession> implements EditSessionService {

    private final EditSessionMapper editSessionMapper;
    private final UserMapper userMapper;

    /**
     * 创建编辑会话（用户进入文件编辑页面 / WebSocket 建立连接时调用）
     * <p>
     * 逻辑说明：
     * 1. 先删除同一 fileId + userId + sessionId 的旧记录，防止重复登录
     * 2. 插入一条新的会话记录
     */
    @Override
    public void createSession(Long fileId, Long userId, String sessionId) {
        // 清理可能存在的旧会话（防止脏数据）
        editSessionMapper.deleteByFileUserSession(fileId, userId, sessionId);
        // 构建新的编辑会话
        EditSession editSession = EditSession.builder()
                .fileId(fileId).userId(userId).sessionId(sessionId)
                .joinedAt(LocalDateTime.now())
                .lastHeartbeat(LocalDateTime.now())
                .build();
        // 写入数据库
        editSessionMapper.insert(editSession);
    }

    /**
     * 关闭单个编辑会话
     */
    @Override
    public void closeSession(Long fileId, Long userId, String sessionId) {
        editSessionMapper.deleteByFileUserSession(fileId, userId, sessionId);
    }

    /**
     * 关闭某个文件的所有编辑会话
     */
    @Override
    public void closeSessionsByFile(Long fileId) {
        editSessionMapper.deleteByFileId(fileId);
    }

    /**
     * 统计当前正在编辑某个文件的在线人数
     */
    @Override
    public int countByFileId(Long fileId) {
        return baseMapper.countByFileId(fileId);
    }

    /**
     * 根据文件ID、用户ID、会话ID查询编辑会话
     */
    @Override
    public EditSession findByFileUserSession(Long fileId, Long userId, String sessionId) {
        return baseMapper.selectByFileUserSession(fileId, userId, sessionId);
    }

    /**
     * 清理过期会话（心跳机制核心方法）
     */
    @Override
    public int cleanExpiredSessions() {
        LocalDateTime deadline = LocalDateTime.now().minusSeconds(30);
        int count = editSessionMapper.deleteExpired(deadline);
        if (count > 0) log.info("清理过期会话 {} 条", count);
        return count;
    }

    /**
     * 通过 fileId 查询在线用户
     */
    @Override
    public Set<String> listByFiledId(Long fileId) {
        Set<String> usernames = new HashSet<>();
        Set<Long> userIds = editSessionMapper.listUserIdsByFileId(fileId);
        for (Long userId : userIds) {
            String username = userMapper.selectUsernameByUserId(userId);
            usernames.add(username);
        }
        if (usernames == null || usernames.isEmpty()) {
            return Collections.emptySet();
        }
        return Set.copyOf(usernames);
    }
}
