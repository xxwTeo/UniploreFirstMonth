package com.xxw.coedit.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxw.coedit.dto.request.ShareCreateDTO;
import com.xxw.coedit.entity.FileShare;
import com.xxw.coedit.common.enums.PermissionEnum;
import com.xxw.coedit.dto.response.SharedFileVO;

import java.util.List;
import java.util.Map;

public interface ShareService extends IService<FileShare> {
    PermissionEnum checkPermission(Long fileId, Long userId);
    void deleteByFileId(Long fileId);
    void shareFile(Long sharedId, ShareCreateDTO shareCreateDTO, Long userId);
    void unShareFile(Long fileId, Long userId);
    Page<SharedFileVO> receivedPage(Page<SharedFileVO> voPage, Long userId);
    // 查询某个文件的所有分享记录（仅文件拥有者可用）
    List<Map<String, Object>> listSharesByFile(Long fileId, Long userId);
}