package com.xxw.coedit.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxw.coedit.dto.request.EditOperationDTO;
import com.xxw.coedit.entity.File;
import com.xxw.coedit.common.enums.PermissionEnum;
import com.xxw.coedit.mapper.FileMapper;
import com.xxw.coedit.service.FileService;
import com.xxw.coedit.service.LockService;
import com.xxw.coedit.service.OnlineUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {
    private final ShareServiceImpl shareService;
    private final LockService lockService;
    private final OnlineUserService onlineUserService;
    @Override
    public File createFile(String name, String content, Long ownerId) {
        File file = File.builder()
                .name(name)
                .content(content)
                .ownerId(ownerId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        save(file);
        return file;
    }
    @Override
    public File getFile(Long fileId, Long userId) {
        PermissionEnum perm = shareService.checkPermission(fileId, userId);
        if (perm == PermissionEnum.NONE) {
            throw new RuntimeException("无权限查看此文件");
        }
        return getById(fileId);
    }
    @Override
    public boolean deleteFile(Long fileId, Long userId) {
        PermissionEnum perm = shareService.checkPermission(fileId, userId);
        if (PermissionEnum.OWNER != perm) {
            throw new RuntimeException("无权限删除该文件");
        }
        shareService.deleteByFileId(fileId);
        return removeById(fileId);
    }
    @Override
    public File updateFile(Long fileId, String content, Long userId) {
        PermissionEnum perm = shareService.checkPermission(fileId, userId);
        if (PermissionEnum.OWNER != perm && PermissionEnum.EDITABLE != perm) {
            throw new RuntimeException("无权限编辑该文件");
        }
        File file = getById(fileId);
        file.setContent(content);
        file.setUpdatedAt(LocalDateTime.now());
        updateById(file);
        return file;
    }
    @Override
    public List<File> myFiles(Long userId) {
        List<File> fileList = list(new LambdaQueryWrapper<File>()
                .eq(File::getOwnerId, userId));
        return fileList;
    }
    @Override
    public String applyOperation(Long fileId, EditOperationDTO op, Long userId) {
        PermissionEnum perm = shareService.checkPermission(fileId, userId);
        if (PermissionEnum.OWNER != perm && PermissionEnum.EDITABLE != perm) {
            throw new RuntimeException("无权限编辑该文件");
        }
        File file = getById(fileId);
        StringBuilder sb = new StringBuilder(file.getContent() == null ? "" : file.getContent());
        if (op.getOp().equals("insert")) {
            sb.insert(op.getPosition(), op.getContent());
        } else if (op.getOp().equals("delete")) {
            sb.delete(op.getPosition(), op.getPosition() + op.getLength());
        }
        file.setContent(sb.toString());
        file.setUpdatedAt(LocalDateTime.now());
        updateById(file);
        return file.getContent();
    }
}