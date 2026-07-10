package com.xxw.coedit.dto.response;
import com.xxw.coedit.common.enums.PermissionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SharedFileVO {
    private Long fileId;
    private String fileName;
    private PermissionEnum permissionEnum;  //当前用户对于文件的权限，0=无权限, 1=只读, 2=可编辑, 3=所有权限
    private String ownerName;
    private LocalDateTime sharedAt;
}