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
    private PermissionEnum permissionEnum;
    private String ownerName;
    private LocalDateTime sharedAt;
}