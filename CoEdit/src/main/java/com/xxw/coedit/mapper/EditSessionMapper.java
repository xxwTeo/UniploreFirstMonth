package com.xxw.coedit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxw.coedit.entity.EditSession;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Set;

public interface EditSessionMapper extends BaseMapper<EditSession> {

    // 删除指定文件+用户+会话的记录
    void deleteByFileUserSession(@Param("fileId") Long fileId,
                                 @Param("userId") Long userId,
                                 @Param("sessionId") String sessionId);

    // 删除某文件的所有会话
    @Delete("DELETE FROM edit_session WHERE file_id = #{fileId}")
    void deleteByFileId(Long fileId);

    // 统计某文件的在线编辑人数
    @Select("SELECT COUNT(*) FROM edit_session WHERE file_id = #{fileId}")
    int countByFileId(Long fileId);

    // 查询指定会话
    EditSession selectByFileUserSession(@Param("fileId") Long fileId,
                                        @Param("userId") Long userId,
                                        @Param("sessionId") String sessionId);

    // 清理超时会话（心跳过期）
    @Delete("DELETE FROM edit_session WHERE last_heartbeat < #{before}")
    int deleteExpired(LocalDateTime before);

    @Select("SELECT user_id from edit_session where file_id = #{fileId}")
    Set<Long> listUserIdsByFileId(Long fileId);
}