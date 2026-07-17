package com.xxw.coedit.mapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxw.coedit.entity.FileShare;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FileShareMapper extends BaseMapper<FileShare> {
    // 查询 fileId 与 userId 之间的分享关系
    FileShare selectByFileIdAndUserId(
            @Param("fileId") Long fileId,
            @Param("userId") Long userId
    );

    // 按照 fileId 删除 FileShare 表中的数据
    void deleteByFileId(Long fileId);

    // 按照 userId 批量查找 FileShare 数据
    Page<FileShare> listPageByUserId(
            Page<FileShare> page,
            @Param("userId") Long userId)
    ;
}