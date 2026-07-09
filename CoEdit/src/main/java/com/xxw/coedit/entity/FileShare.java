package com.xxw.coedit.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xxw.coedit.common.enums.PermissionEnum;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
@Builder
@Data
@TableName("file_shares")
public class FileShare {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long fileId;
    private PermissionEnum permission;
    private LocalDateTime createdAt;
}