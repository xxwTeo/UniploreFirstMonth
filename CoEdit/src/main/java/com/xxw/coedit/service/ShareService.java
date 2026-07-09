package com.xxw.coedit.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxw.coedit.entity.FileShare;
import com.xxw.coedit.common.enums.PermissionEnum;
import com.xxw.coedit.dto.response.SharedFileVO;
public interface ShareService extends IService<FileShare> {
    PermissionEnum checkPermission(Long fileId, Long userId);
    void deleteByFileId(Long fileId);
    void shareFile(Long fileId, Long targetUserId, PermissionEnum perm, Long userId);
    void unShareFile(Long fileId, Long userId);
    Page<SharedFileVO> receviedPage(Page<SharedFileVO> voPage, Long userId);
}