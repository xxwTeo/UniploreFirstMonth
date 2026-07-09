package com.xxw.coedit.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxw.coedit.entity.User;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface UserMapper extends BaseMapper<User> {
}