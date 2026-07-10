package com.xxw.coedit.dto.request;

import lombok.Data;

@Data
public class EditOperationDTO {
    private Long fileId;    //进行编辑的文件id
    private String op;      //进行的操作类型 insert / delete
    private Integer position;   //进行操作的光标位置
    private String content;     //插入的内容
    private Integer length;     //删除的字符串长度( insert 时为0)
}
