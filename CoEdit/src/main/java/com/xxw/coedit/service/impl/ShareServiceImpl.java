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
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl extends ServiceImpl<FileShareMapper, FileShare> implements ShareService {

    private final FileMapper fileMapper;
    private final UserMapper userMapper;

    /**
     * 校验用户对指定文件的权限等级
     * 文件权限, 0=无权限, 1=只读, 2=可编辑, 4=所有权限
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 所有者权限
     */
    @Override
    public PermissionEnum checkPermission(Long fileId, Long userId) {
        File file = fileMapper.selectById(fileId);
        // 文件不存在视为无权限，避免上层重复判断
        if (file == null) return PermissionEnum.NONE;
        // 拥有者返回最高权限
        if (file.getOwnerId().equals(userId)) return PermissionEnum.OWNER;
        // 查询分享记录，存在即返回对应权限
        FileShare fileShare = getOne(new LambdaQueryWrapper<FileShare>()
                .eq(FileShare::getFileId, fileId)
                .eq(FileShare::getUserId, userId));
        if (fileShare != null) return fileShare.getPermission();
        return PermissionEnum.NONE;
    }

    /**
     * 根据 fileId 删除相应的分享关系
     * @param fileId 被删除的 fileId
     */
    @Override
    public void deleteByFileId(Long fileId) {
        remove(new LambdaQueryWrapper<FileShare>()
                .eq(FileShare::getFileId, fileId));
    }

    /**
     * 分享文件给其他用户
     * @param fileId        文件ID
     * @param targetUserId  被分享的目标用户ID
     * @param perm          当前用户对文件的权限
     * @param userId        当前操作用户ID
     * @throws BizException 当文件不存在、无权限、或分享给自己时抛出业务异常
     */
    @Override
    public void shareFile(Long fileId, Long targetUserId, PermissionEnum perm, Long userId) {
        File file = fileMapper.selectById(fileId);
        // 文件不存在或非拥有者禁止分享
        if (file == null || !file.getOwnerId().equals(userId)) {
            throw new BizException(4001, "文件不存在或无权限");
        }
        // 防止自己分享给自己
        if (targetUserId.equals(file.getOwnerId())) {
            throw new BizException(4002, "不能分享文件给自己");
        }
        // 已存在分享关系则更新权限，避免重复数据
        FileShare exist = getOne(new LambdaQueryWrapper<FileShare>()
                .eq(FileShare::getFileId, fileId)
                .eq(FileShare::getUserId, targetUserId));
        if (exist != null) {
            exist.setPermission(perm);
            updateById(exist);
            return;
        }
        // 新建分享关系
        FileShare share = FileShare.builder()
                .fileId(fileId)
                .userId(targetUserId)
                .permission(perm)
                .createdAt(LocalDateTime.now())
                .build();
        save(share);
    }

    /**
     * 取消对文件的分享
     * @param fileId 分享文件的文件id
     * @param userId 操作的用户id
     */
    @Override
    public void unShareFile(Long fileId, Long userId) {
        // 查询分享记录（shareId 是主键，必然唯一）
        FileShare fileShare = getById(fileId);
        if (fileShare == null) {
            throw new BizException(40003, "该分享不存在");
        }
        // 查询对应文件
        File file = fileMapper.selectById(fileShare.getFileId());
        // 只有文件拥有者可以取消分享
        if (file == null || !file.getOwnerId().equals(userId)) {
            throw new BizException(40004, "无权限取消分享");
        }
        removeById(fileId);
    }

    /**
     * 查询当前用户收到的分享文件（分页 + VO 转换）
     * @param voPage 分页参数（当前页、每页条数），由前端传入
     * @param userId 当前登录用户ID
     * @return 分页后的分享文件视图对象
     */
        @Override
        public Page<SharedFileVO> receviedPage(Page<SharedFileVO> voPage, Long userId) {
            // 1. 分页查询当前用户收到的所有分享记录
            Page<FileShare> sharePage = page(
                    new Page<>(voPage.getCurrent(), voPage.getSize()),
                    new LambdaQueryWrapper<FileShare>()
                            .eq(FileShare::getUserId, userId)
                            .orderByDesc(FileShare::getCreatedAt)
            );

            // 2. 无分享记录时直接返回空分页
            if (sharePage.getRecords().isEmpty()) {
                return new Page<>(voPage.getCurrent(), voPage.getSize());
            }

            // 3. 收集文件fileIds，批量查询文件信息（减少 DB 交互）
            Set<Long> fileIds = sharePage.getRecords().stream()
                    .map(FileShare::getFileId)
                    .collect(Collectors.toSet());

            Map<Long, File> fileMap = fileMapper.selectBatchIds(fileIds).stream()
                    .collect(Collectors.toMap(
                            File::getId,
                            Function.identity(),
                            (k1, k2) -> k1)
                    );

            // 4. 收集文件拥有者userIds，批量查询用户名
            Set<Long> userIds = fileMap.values().stream()
                    .map(File::getOwnerId)
                    .collect(Collectors.toSet());

            Map<Long, String> userMap = userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(
                            User::getId,
                            User::getUsername,
                            (k1, k2) -> k1)
                    );

            // 5. 组装 VO 列表
            List<SharedFileVO> voList = new ArrayList<>();
            for (FileShare share : sharePage.getRecords()) {
                File file = fileMap.get(share.getFileId());
                // 文件已被删除则跳过
                if (file == null) {
                    continue;
                }

                SharedFileVO vo = SharedFileVO.builder()
                        .fileId(file.getId())
                        .fileName(file.getName())
                        .permissionEnum(PermissionEnum.fromCode(share.getPermission().getCode()))
                        .sharedAt(share.getCreatedAt())
                        .ownerName(userMap.getOrDefault(file.getOwnerId(), "未知用户"))
                        .build();
                voList.add(vo);
            }
            // 6. 回填分页结果
            voPage.setTotal(sharePage.getTotal());
            voPage.setRecords(voList);
            return voPage;
        }
}
