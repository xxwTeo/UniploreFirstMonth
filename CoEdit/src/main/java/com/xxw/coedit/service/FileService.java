package com.xxw.coedit.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxw.coedit.dto.request.EditOperationDTO;
import com.xxw.coedit.entity.File;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

public interface FileService extends IService<File> {
    // 创建文本文件
    File createFile(String name, String content, Long ownerId);
    // 获取文件详情
    File getFile(Long fileId, Long userId);
    // 删除相应的文件和对应分享内容
    boolean deleteFile(Long fileId, Long userId);
    //更新文件
    File updateFile(Long filedId, String content, Long userId, String sessionId);
    // 查询用户的所有文件
    Page<File> myFiles(Page<File> filePage, Long userId);
    // 协同修改文件
    String applyOperation(Long fileId, EditOperationDTO op, Long userId);
}
