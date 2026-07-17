package com.xxw.coedit.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxw.coedit.common.enums.ErrorCode;
import com.xxw.coedit.common.exceptions.BizException;
import com.xxw.coedit.dto.request.EditOperationDTO;
import com.xxw.coedit.entity.File;
import com.xxw.coedit.common.enums.PermissionEnum;
import com.xxw.coedit.mapper.FileMapper;
import com.xxw.coedit.security.SessionAccessGate;
import com.xxw.coedit.service.FileService;
import com.xxw.coedit.service.ShareService;
import lombok.RequiredArgsConstructor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    private final ShareService shareService;
    private final FileMapper fileMapper;
    private final SessionAccessGate sessionAccessGate;

    /**
     * 创建新文件
     *
     * @param name    文件名
     * @param content 文件初始内容
     * @param ownerId 所属用户ID（创建者）
     * @return 创建成功的文件实体
     */
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

    /**
     * 查询文件详情
     *
     * @param fileId 文件ID
     * @param userId 当前用户ID
     * @return 文件实体
     * @throws BizException 无浏览权限时抛出
     */
    @Override
    public File getFile(Long fileId, Long userId) {
        PermissionEnum perm = shareService.checkPermission(fileId, userId);
        if (perm == PermissionEnum.NONE) {
            throw new BizException(ErrorCode.NO_VIEW_PERMISSION);
        }
        return getById(fileId);
    }

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @param userId 当前用户ID
     * @return 是否删除成功
     * @throws BizException 非文件所有者时抛出
     */
    @Override
    public boolean deleteFile(Long fileId, Long userId) {
        PermissionEnum perm = shareService.checkPermission(fileId, userId);
        if (PermissionEnum.OWNER != perm) {
            throw new BizException(ErrorCode.NO_DELETE_PERMISSION);
        }
        // 清理分享记录和在线编辑状态
        shareService.deleteByFileId(fileId);
        return removeById(fileId);
    }

    /**
     * 更新文件内容（全量覆盖）
     *
     * @param fileId    文件ID
     * @param content   新内容
     * @param userId    当前用户ID
     * @param sessionId 协同编辑会话ID
     * @return 更新后的文件实体
     * @throws BizException 无编辑权限或会话无效时抛出
     */
    @Override
    public File updateFile(Long fileId, String content, Long userId, String sessionId) {
        sessionAccessGate.ensureEditable(fileId, userId, sessionId);
        PermissionEnum perm = shareService.checkPermission(fileId, userId);
        if (PermissionEnum.OWNER != perm && PermissionEnum.EDITABLE != perm) {
            throw new BizException(ErrorCode.NO_EDIT_PERMISSION);
        }
        File file = getById(fileId);
        file.setContent(content);
        file.setUpdatedAt(LocalDateTime.now());
        updateById(file);
        return file;
    }

    /**
     * 分页查询当前用户的文件列表
     *
     * @param filePage 分页参数
     * @param userId   当前用户ID
     * @return 文件分页结果
     */
    @Override
    public Page<File> myFiles(Page<File> filePage, Long userId) {
        return fileMapper.listFilesPage(filePage, userId);
    }

    /**
     * 应用协同编辑操作（OT 操作）
     *
     * @param fileId 文件ID
     * @param op     编辑操作（insert / delete / replace）
     * @param userId 当前用户ID
     * @return 操作后的文件内容
     * @throws BizException 无编辑权限时抛出
     */
    @Override
    public String applyOperation(Long fileId, EditOperationDTO op, Long userId) {
        // 校验用户是否具备编辑权限（所有者或可编辑成员）
        PermissionEnum perm = shareService.checkPermission(fileId, userId);
        if (PermissionEnum.OWNER != perm && PermissionEnum.EDITABLE != perm) {
            throw new BizException(ErrorCode.NO_EDIT_PERMISSION);
        }

        // 加载当前文件快照
        File file = getById(fileId);

        // 处理空内容，避免 NPE
        StringBuilder sb = new StringBuilder(file.getContent() == null ? "" : file.getContent());

        // 根据操作类型执行对应文本变更
        if (op.getOp().equals("insert")) {
            // 在指定位置插入内容
            sb.insert(op.getPosition(), op.getContent());

        } else if (op.getOp().equals("delete")) {
            // 从指定位置删除指定长度的文本
            sb.delete(op.getPosition(), op.getPosition() + op.getLength());

        } else if (op.getOp().equals("replace")) {
            // 全量替换：前端直接下发完整内容（如回滚、重置）
            file.setContent(op.getContent());
            file.setUpdatedAt(LocalDateTime.now());
            updateById(file);
            return file.getContent();
        }

        // 增量操作后写回文件内容
        file.setContent(sb.toString());
        file.setUpdatedAt(LocalDateTime.now());
        updateById(file);

        return file.getContent();
    }
}