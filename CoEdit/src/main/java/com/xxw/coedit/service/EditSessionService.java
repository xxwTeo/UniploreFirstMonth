package com.xxw.coedit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxw.coedit.entity.EditSession;

import java.util.Set;

public interface EditSessionService extends IService<EditSession> {
    // 创建编辑会话（用户进入文件编辑页面 / WebSocket 建立连接时调用）
    void createSession(Long fileId, Long userId, String sessionId);

    // 关闭单个编辑会话
    void closeSession(Long fileId, Long userId, String sessionId);

    // 关闭某个文件的所有编辑会话
    void closeSessionsByFile(Long fileId);

    // 统计当前正在编辑某个文件的在线人数
    int countByFileId(Long fileId);

    // 根据文件ID、用户ID、会话ID查询编辑会话
    EditSession findByFileUserSession(Long fileId, Long userId, String sessionId);

    // 清理过期会话
    int cleanExpiredSessions();

    // 通过 fileId 查询在线用户
    Set<String> listByFiledId(Long fileId);
}
