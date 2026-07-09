package com.xxw.coedit.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxw.coedit.dto.request.RegisterDTO;
import com.xxw.coedit.entity.User;
public interface UserService extends IService<User> {
    void register(RegisterDTO registerDTO);
    String login(RegisterDTO registerDTO);
    User profile(Long userId);
}