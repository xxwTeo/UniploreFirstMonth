package com.xxw.coedit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxw.coedit.dto.request.RegisterDTO;
import com.xxw.coedit.entity.User;

public interface UserService extends IService<User> {
    //用户注册
    void register(RegisterDTO registerDTO);
    //用户登录接口
    String login(RegisterDTO registerDTO);
    //获取用户信息
    User profile(Long userId);
}
