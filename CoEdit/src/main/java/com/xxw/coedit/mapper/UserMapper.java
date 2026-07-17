package com.xxw.coedit.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxw.coedit.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 通过 username 查询用户
    User selectUserByUsername(@NotBlank String username);

    @Select("select username from users where id = #{userId}")
    String selectUsernameByUserId(Long userId);

}