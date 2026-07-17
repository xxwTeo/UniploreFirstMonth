package com.xxw.coedit.dto.request;

import com.xxw.coedit.common.enums.PermissionEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class ShareCreateDTO {
    @NotNull
    private String targetUsername;

    private Long targetUserId;

    @NotNull
    private PermissionEnum permission;
}
