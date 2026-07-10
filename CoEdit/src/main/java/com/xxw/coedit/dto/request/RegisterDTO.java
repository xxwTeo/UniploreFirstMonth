package com.xxw.coedit.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterDTO {      //注册信息
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
