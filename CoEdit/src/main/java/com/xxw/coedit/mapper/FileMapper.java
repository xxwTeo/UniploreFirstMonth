package com.xxw.coedit.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxw.coedit.entity.File;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FileMapper extends BaseMapper<File> {

    // 批量查询用户拥有的 File
    Page<File> listFilesPage(
            Page<File> filePage,
            @Param("userId") Long userId);
}