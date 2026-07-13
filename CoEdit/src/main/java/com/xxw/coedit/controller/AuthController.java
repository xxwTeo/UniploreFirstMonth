package com.xxw.coedit.controller;
import com.xxw.coedit.dto.request.LoginDTO;
import com.xxw.coedit.security.JwtUtil;
import com.xxw.coedit.dto.request.RegisterDTO;
import com.xxw.coedit.entity.User;
import com.xxw.coedit.security.WebUtils;
import com.xxw.coedit.service.UserService;
import com.xxw.coedit.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final WebUtils webUtils;

    /**
     * 用户注册
     * 接收前端传来的用户名和密码
     * @param registerDTO
     * @return 注册成功响应
     */
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.succeed();
    }

    /**
     * 用户登录接口
     * 接收前端传来的用户名和密码，验证成功后返回 JWT token
     * @param loginDTO 登录请求体，包含用户名和密码
     * @return 登录成功返回 token，失败抛出异常
     */
    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        return Result.succeed(token);
    }

    /**
     * 获取当前登录用户的基本信息
     * @param request HTTP 请求（用于解析当前登录用户）
     * @return 当前用户详细信息
     */
    @GetMapping("/profile")
    public Result<?> profile(HttpServletRequest request) {
        Long userId = webUtils.currentUserId(request);
        User user = userService.profile(userId);
        return Result.succeed(user);
    }
}
