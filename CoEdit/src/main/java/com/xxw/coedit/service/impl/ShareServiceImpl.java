package com.xxw.coedit.service.impl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxw.coedit.common.enums.ErrorCode;
import com.xxw.coedit.dto.request.ShareCreateDTO;
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
    private final FileShareMapper fileShareMapper;

    /**
     * 校验用户对某文件的权限级别
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 权限枚举（OWNER / EDITABLE / VIEWABLE / NONE）
     */
    @Override
    public PermissionEnum checkPermission(Long fileId, Long userId) {
        File file = fileMapper.selectById(fileId);
        if (file == null) {
            return PermissionEnum.NONE;
        }
        // 文件所有者
        if (file.getOwnerId().equals(userId)) {
            return PermissionEnum.OWNER;
        }
        // 被分享用户
        FileShare fileShare = fileShareMapper.selectByFileIdAndUserId(fileId, userId);
        if (fileShare != null && fileShare.getPermission() != null) {
            return fileShare.getPermission();
        }
        return PermissionEnum.NONE;
    }

    /**
     * 删除某文件的所有分享记录 (级联清理)
     *
     * @param fileId 文件ID
     */
    @Override
    public void deleteByFileId(Long fileId) {
        fileShareMapper.deleteByFileId(fileId);
    }

    /**
     * 分享文件给指定用户
     *
     * @param fileId          文件ID
     * @param shareCreateDTO  分享参数（目标用户名、权限）
     * @param userId          当前操作用户ID（必须是文件所有者）
     */
    @Override
    public void shareFile(Long fileId, ShareCreateDTO shareCreateDTO, Long userId) {
        // 校验文件是否存在且归属当前用户
        File file = fileMapper.selectById(fileId);
        if (file == null || !file.getOwnerId().equals(userId)) {
            throw new BizException(ErrorCode.SHARE_FILE_NOT_FOUND);
        }

        // 解析目标用户
        User user = userMapper.selectUserByUsername(shareCreateDTO.getTargetUsername());
        shareCreateDTO.setTargetUserId(user.getId());

        // 禁止分享给自己
        if (shareCreateDTO.getTargetUserId().equals(userId)) {
            throw new BizException(ErrorCode.SHARE_TO_SELF);
        }

        // 已存在分享关系则更新权限，否则新增
        FileShare exist = fileShareMapper.selectByFileIdAndUserId(fileId, shareCreateDTO.getTargetUserId());
        if (exist != null) {
            exist.setPermission(shareCreateDTO.getPermission());
            updateById(exist);
            return;
        }

        FileShare share = FileShare.builder()
                .fileId(fileId)
                .userId(shareCreateDTO.getTargetUserId())
                .permission(shareCreateDTO.getPermission())
                .createdAt(LocalDateTime.now())
                .build();
        save(share);
    }

    /**
     * 取消文件分享
     *
     * @param sharedId 分享记录ID
     * @param userId   当前操作用户ID（所有者或被分享人均可取消）
     */
    @Override
    public void unShareFile(Long sharedId, Long userId) {
        FileShare fileShare = getById(sharedId);
        if (fileShare == null) {
            throw new BizException(ErrorCode.SHARE_NOT_EXIST);
        }

        File file = fileMapper.selectById(fileShare.getFileId());
        boolean isOwner = file.getOwnerId().equals(userId);
        boolean isSharedUser = fileShare.getUserId().equals(userId);

        if (file == null || (!isOwner && !isSharedUser)) {
            throw new BizException(ErrorCode.SHARE_NO_PERMISSION);
        }
        removeById(sharedId);
    }

    /**
     * 查询某文件的所有分享记录（仅所有者可见）
     *
     * @param fileId 文件ID
     * @param userId 当前用户ID
     * @return 分享记录列表（包含用户名、权限）
     */
    @Override
    public List<Map<String, Object>> listSharesByFile(Long fileId, Long userId) {
        // 1. 校验文件是否存在，且当前用户是文件所有者（只有所有者能查看分享列表）
        File file = fileMapper.selectById(fileId);
        if (file == null || !file.getOwnerId().equals(userId)) {
            throw new BizException(ErrorCode.SHARE_FILE_NOT_FOUND);
        }

        // 2. 查询该文件的所有分享记录
        List<FileShare> shares = lambdaQuery()
                .eq(FileShare::getFileId, fileId)
                .list();
        if (shares.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 批量查询被分享用户的用户名（避免 N+1 查询问题）
        Set<Long> targetIds = shares.stream()
                .map(FileShare::getUserId)
                .collect(Collectors.toSet());
        Map<Long, String> userMap = userMapper.selectBatchIds(targetIds).stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));

        // 4. 组装返回结果：分享ID + 被分享人用户名 + 权限级别
        List<Map<String, Object>> result = new ArrayList<>();
        for (FileShare share : shares) {
            Map<String, Object> item = new HashMap<>();
            item.put("shareId", share.getId());
            item.put("targetUsername", userMap.getOrDefault(share.getUserId(), "未知用户"));
            item.put("permission", share.getPermission().name());
            result.add(item);
        }
        return result;
    }

    /**
     * 分页查询当前用户收到的分享文件
     *
     * @param voPage 分页参数
     * @param userId 当前用户ID
     * @return 分页后的分享文件视图（含文件名、所有者、权限、分享时间）
     */
    @Override
    public Page<SharedFileVO> receivedPage(Page<SharedFileVO> voPage, Long userId) {
        // 1. 查询当前用户的分享记录
        Page<FileShare> sharePage = fileShareMapper.listPageByUserId(
                new Page<>(voPage.getCurrent(), voPage.getSize()), userId);

        if (sharePage.getRecords().isEmpty()) {
            voPage.setTotal(0L);
            voPage.setRecords(Collections.emptyList());
            return voPage;
        }

        // 2. 批量查询文件信息
        Set<Long> fileIds = sharePage.getRecords().stream()
                .map(FileShare::getFileId).collect(Collectors.toSet());
        Map<Long, File> fileMap = fileMapper.selectBatchIds(fileIds).stream()
                .collect(Collectors.toMap(File::getId, Function.identity(), (k1, k2) -> k1));

        // 3. 批量查询文件所有者用户名
        Set<Long> ownerIds = fileMap.values().stream().map(File::getOwnerId).collect(Collectors.toSet());
        Map<Long, String> ownerMap = userMapper.selectBatchIds(ownerIds).stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));

        // 4. 组装 VO
        List<SharedFileVO> voList = new ArrayList<>();
        for (FileShare share : sharePage.getRecords()) {
            File file = fileMap.get(share.getFileId());
            if (file == null) continue;

            SharedFileVO vo = SharedFileVO.builder()
                    .shareId(share.getId())
                    .fileId(file.getId())
                    .fileName(file.getName())
                    .permissionEnum(PermissionEnum.fromCode(share.getPermission().getCode()))
                    .sharedAt(share.getCreatedAt())
                    .ownerName(ownerMap.getOrDefault(file.getOwnerId(), "未知用户"))
                    .build();
            voList.add(vo);
        }

        // 5. 封装返回
        voPage.setTotal(sharePage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }
}