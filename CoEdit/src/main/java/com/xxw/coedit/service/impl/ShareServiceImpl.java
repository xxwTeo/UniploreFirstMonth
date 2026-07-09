package com.xxw.coedit.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxw.coedit.entity.File;
import com.xxw.coedit.entity.FileShare;
import com.xxw.coedit.entity.User;
import com.xxw.coedit.common.enums.PermissionEnum;
import com.xxw.coedit.common.exceptions.BizException;
import com.xxw.coedit.mapper.FileMapper;
import com.xxw.coedit.mapper.FileShareMapper;
import com.xxw.coedit.mapper.UserMapper;
import com.xxw.coedit.service.ShareService;
import com.xxw.coedit.dto.response.SharedFileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class ShareServiceImpl extends ServiceImpl<FileShareMapper, FileShare> implements ShareService {
    private final FileMapper fileMapper;
    private final UserMapper userMapper;
    @Override
    public PermissionEnum checkPermission(Long fileId, Long userId) {
        File file = fileMapper.selectById(fileId);
        if (file == null) return PermissionEnum.NONE;
        if (file.getOwnerId().equals(userId)) return PermissionEnum.OWNER;
        FileShare fileShare = getOne(new LambdaQueryWrapper<FileShare>()
                .eq(FileShare::getFileId, fileId)
                .eq(FileShare::getUserId, userId));
        if (fileShare != null) return fileShare.getPermission();
        return PermissionEnum.NONE;
    }
    @Override
    public void deleteByFileId(Long fileId) {
        remove(new LambdaQueryWrapper<FileShare>()
                .eq(FileShare::getFileId, fileId));
    }
    @Override
    public void shareFile(Long fileId, Long targetUserId, PermissionEnum perm, Long userId) {
        File file = fileMapper.selectById(fileId);
        if (file == null || !file.getOwnerId().equals(userId)) {
            throw new BizException(4001, "文件不存在或无权限");
        }
        if (targetUserId.equals(file.getOwnerId())) {
            throw new BizException(4002, "不能分享文件给自己");
        }
        FileShare exist = getOne(new LambdaQueryWrapper<FileShare>()
                .eq(FileShare::getFileId, fileId)
                .eq(FileShare::getUserId, targetUserId));
        if (exist != null) {
            exist.setPermission(perm);
            updateById(exist);
            return;
        }
        FileShare share = FileShare.builder()
                .fileId(fileId)
                .userId(targetUserId)
                .permission(perm)
                .createdAt(LocalDateTime.now())
                .build();
        save(share);
    }
    @Override
    public void unShareFile(Long fileId, Long userId) {
        FileShare fileShare = getById(fileId);
        if (fileShare == null) {
            throw new BizException(40003, "该分享不存在");
        }
        File file = fileMapper.selectById(fileShare.getFileId());
        if (file == null || !file.getOwnerId().equals(userId)) {
            throw new BizException(40004, "无权限取消分享");
        }
        removeById(fileId);
    }
    @Override
    public Page<SharedFileVO> receviedPage(Page<SharedFileVO> voPage, Long userId) {
        Page<FileShare> sharePage = page(
                new Page<>(voPage.getCurrent(), voPage.getSize()),
                new LambdaQueryWrapper<FileShare>()
                        .eq(FileShare::getUserId, userId)
                        .orderByDesc(FileShare::getCreatedAt));
        if (sharePage.getRecords().isEmpty()) {
            return new Page<>(voPage.getCurrent(), voPage.getSize());
        }
        Set<Long> fileIds = sharePage.getRecords().stream()
                .map(FileShare::getFileId).collect(Collectors.toSet());
        Map<Long, File> fileMap = fileMapper.selectBatchIds(fileIds).stream()
                .collect(Collectors.toMap(File::getId, f -> f));
        Set<Long> userIds = fileMap.values().stream()
                .map(File::getOwnerId).collect(Collectors.toSet());
        Map<Long, String> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));
        List<SharedFileVO> voList = new ArrayList<>();
        for (FileShare share : sharePage.getRecords()) {
            File file = fileMapper.selectById(share.getFileId());
            if (file == null) continue;
            SharedFileVO vo = SharedFileVO.builder()
                    .fileId(file.getId())
                    .fileName(file.getName())
                    .permissionEnum(PermissionEnum.fromCode(share.getPermission().getCode()))
                    .sharedAt(share.getCreatedAt())
                    .ownerName(userMap.getOrDefault(file.getOwnerId(), "未知用户"))
                    .build();
            voList.add(vo);
        }
        voPage.setTotal(sharePage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }
}