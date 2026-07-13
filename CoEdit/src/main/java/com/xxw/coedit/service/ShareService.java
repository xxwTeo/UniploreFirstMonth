package com.xxw.coedit.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxw.coedit.dto.request.ShareCreateDTO;
import com.xxw.coedit.entity.FileShare;
import com.xxw.coedit.common.enums.PermissionEnum;
import com.xxw.coedit.dto.response.SharedFileVO;

public interface ShareService extends IService<FileShare> {
    // 判断 userId 对于 filedId 的权限
    PermissionEnum checkPermission(Long fileId, Long userId);
    // 删除文件时根据 fileId 清理分享关系
    void deleteByFileId(Long fileId);
    // 分享文件
    void shareFile(Long sharedId, ShareCreateDTO shareCreateDTO, Long userId);
    // 取消对文件的分享
    void unShareFile(Long fileId, Long userId);
    // 查询当前用户收到的所有分享文件
    Page<SharedFileVO> receivedPage(Page<SharedFileVO> voPage, Long userId);
}
