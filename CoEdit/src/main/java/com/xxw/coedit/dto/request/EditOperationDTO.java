package com.xxw.coedit.dto.request;
import lombok.Data;
@Data
public class EditOperationDTO {
    private Long fileId;
    private String op;
    private Integer position;
    private String content;
    private Integer length;
}