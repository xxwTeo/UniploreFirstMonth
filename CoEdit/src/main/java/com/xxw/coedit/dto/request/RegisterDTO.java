package com.xxw.coedit.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class RegisterDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}