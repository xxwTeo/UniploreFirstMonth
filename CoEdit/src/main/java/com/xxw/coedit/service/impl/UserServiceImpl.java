package com.xxw.coedit.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxw.coedit.common.enums.ErrorCode;
import com.xxw.coedit.common.exceptions.BizException;
import com.xxw.coedit.dto.request.LoginDTO;
import com.xxw.coedit.security.JwtUtil;
import com.xxw.coedit.dto.request.RegisterDTO;
import com.xxw.coedit.entity.User;
import com.xxw.coedit.mapper.UserMapper;
import com.xxw.coedit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;

    /**
     * BCrypt 密码加密器，用于密码的加密存储和登录验证
     */
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 注册用户
     * @param registerDTO 注册请求参数，包含用户名和明文密码
     */
    @Override
    public void register(RegisterDTO registerDTO) {
        User user = User.builder()
                .username(registerDTO.getUsername())
                .password(encoder.encode(registerDTO.getPassword()))
                .createdAt(LocalDateTime.now())
                .build();
        save(user);
    }

    /**
     * 用户登录
     * @param loginDTO 登录请求参数，包含用户名和明文密码
     * @return 用户 token
     */
    @Override
    public String login(LoginDTO loginDTO) {
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, loginDTO.getUsername()));
        if (user == null || !encoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BizException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }
        return jwtUtil.generateToken(user.getId(), user.getUsername());
    }

    /**
     * 根据 userId 查询返回 user
     * @param userId 前端 token 中获取
     * @return user 对象 (将 password 设置为 null)
     */
    @Override
    public User profile(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }
        user.setPassword(null);
        return user;
    }
}
