package com.xxw.coedit.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxw.coedit.dto.request.EditOperationDTO;
import com.xxw.coedit.entity.File;
import java.util.List;
public interface FileService extends IService<File> {
    File createFile(String name, String content, Long ownerId);
    File getFile(Long fileId, Long userId);
    boolean deleteFile(Long fileId, Long userId);
    File updateFile(Long filedId, String content, Long userId);
    List<File> myFiles(Long userId);
    String applyOperation(Long fileId, EditOperationDTO op, Long userId);
}