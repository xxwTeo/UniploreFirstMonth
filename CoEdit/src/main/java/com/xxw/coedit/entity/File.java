package com.xxw.coedit.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("files")
public class File {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;        //文件名
    private String content;     //文件内容
    private Long ownerId;       //所属人id
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
