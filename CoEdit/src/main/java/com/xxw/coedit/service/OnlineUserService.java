package com.xxw.coedit.service;

import java.util.Set;

public interface OnlineUserService {
    // 用户进入编辑
    void enterEdit(Long fileId, Long userId);
    // 用户退出编辑
    void exitEdit(Long fileId, Long userId);
    // 获取当前正在编辑的用户
    Set<Object> getEditors(Long fileId);
    // 根据文件ID清理在线编辑状态
    Boolean clearFileEditors(Long fileId);
}