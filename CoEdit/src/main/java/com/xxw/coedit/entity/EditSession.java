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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("edit_session")
public class EditSession {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long fileId;        // 文件ID
    private Long userId;        // 用户ID
    private String sessionId;   // 客户端会话ID（区分多标签页）

    private LocalDateTime joinedAt;      // 加入时间
    private LocalDateTime lastHeartbeat; // 最后心跳时间（判活）
}
