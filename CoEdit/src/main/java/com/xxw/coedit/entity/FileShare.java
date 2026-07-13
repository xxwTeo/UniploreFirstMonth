package com.xxw.coedit.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
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
    private Long userId;        //被分享者
    private Long fileId;
    @EnumValue
    private PermissionEnum permission;      //文件权限, 0=无权限, 1=只读, 2=可编辑, 3=所有权限
    private LocalDateTime createdAt;
}
