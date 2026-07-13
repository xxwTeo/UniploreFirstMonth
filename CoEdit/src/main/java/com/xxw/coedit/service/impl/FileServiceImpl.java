package com.xxw.coedit.service.impl;
import ch.qos.logback.core.spi.ErrorCodes;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxw.coedit.common.enums.ErrorCode;
import com.xxw.coedit.common.exceptions.BizException;
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

    /**
     * 创建文本文件
     * @param name     文件名
     * @param content  文件内容
     * @param ownerId  文件所属用户 ID（从 Token 中解析）
     * @return 创建成功的文件实体（包含自增生成的文件 ID）
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
     * 根据用户ID获取指定文件，并进行权限校验
     * @param fileId 文件ID
     * @param userId 当前登录用户ID
     * @return 文件实体
     * @throws BizException 当用户对该文件无访问权限时抛出
     */
    @Override
    public File getFile(Long fileId, Long userId) {
        // 校验权限，失败直接抛异常
        PermissionEnum perm = shareService.checkPermission(fileId, userId);
        if (perm == PermissionEnum.NONE) {
            throw new BizException(ErrorCode.NO_VIEW_PERMISSION);
        }
        return getById(fileId);
    }

    /**
     * 删除文件和对应的分享记录
     * @param fileId 被删文件
     * @param userId 操作人的 userId
     * @return 是否删除成功
     */
    @Override
    public boolean deleteFile(Long fileId, Long userId) {
        // 校验权限，失败直接抛异常
        PermissionEnum perm = shareService.checkPermission(fileId, userId);
        if (PermissionEnum.OWNER != perm) {
            throw new BizException(ErrorCode.NO_DELETE_PERMISSION);
        }
        shareService.deleteByFileId(fileId);
        lockService.releaseLock(fileId, userId);
        onlineUserService.clearFileEditors(fileId);
        return removeById(fileId);
    }

    /**
     * 更新文件内容（带权限校验）
     * @param fileId 文件ID
     * @param content 新的文件内容
     * @param userId  当前操作用户ID
     * @return 更新后的文件实体
     */
    @Override
    public File updateFile(Long fileId, String content, Long userId) {
        // 校验权限，失败直接抛异常
        PermissionEnum perm = shareService.checkPermission(fileId, userId);
        if (PermissionEnum.OWNER != perm && PermissionEnum.EDITABLE != perm) {
            throw new BizException(ErrorCode.NO_EDIT_PERMISSION);
        }
        // 查询文件
        File file = getById(fileId);
        // 全量覆盖内容
        file.setContent(content);
        file.setUpdatedAt(LocalDateTime.now());
        updateById(file);
        return file;
    }

    /**
     * 查询当前用户拥有的所有文件
     * @param userId 当前用户ID
     * @return 当前用户拥有的文件列表
     * TODO 改进为分页
     */
    @Override
    public List<File> myFiles(Long userId) {
        List<File> fileList = list(new LambdaQueryWrapper<File>()
                .eq(File::getOwnerId, userId));
        return fileList;
    }

    /**
     * 应用单次编辑操作（插入/删除）
     * @param fileId 文件ID
     * @param op     编辑操作DTO（insert / delete）
     * @param userId 当前操作用户ID
     * @return 编辑后的文件内容
     */
    @Override
    public String applyOperation(Long fileId, EditOperationDTO op, Long userId) {
        // 权限校验：只有可编辑或拥有者才能执行 OT 操作
        PermissionEnum perm = shareService.checkPermission(fileId, userId);
        if (PermissionEnum.OWNER != perm && PermissionEnum.EDITABLE != perm) {
            throw new BizException(ErrorCode.NO_EDIT_PERMISSION);
        }
        // 查询当前文件快照
        File file = getById(fileId);
        // 数据库里 content 可能为 null，防御性处理
        StringBuilder sb = new StringBuilder(file.getContent() == null ? "" : file.getContent());
        // 根据操作类型执行原子编辑
        if (op.getOp().equals("insert")) {
            // insert 基于光标位置，不依赖上下文
            sb.insert(op.getPosition(), op.getContent());
        } else if (op.getOp().equals("delete")) {
            // delete 必须保证区间合法,(后端暂不二次校验)
            sb.delete(op.getPosition(), op.getPosition() + op.getLength());
        }
        // 写回新内容
        file.setContent(sb.toString());
        file.setUpdatedAt(LocalDateTime.now());
        updateById(file);
        return file.getContent();
    }
}
