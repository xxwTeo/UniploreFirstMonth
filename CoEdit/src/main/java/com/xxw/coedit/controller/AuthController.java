package com.xxw.coedit.controller;
import com.xxw.coedit.security.JwtUtil;
import com.xxw.coedit.dto.request.RegisterDTO;
import com.xxw.coedit.entity.User;
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
    private final JwtUtil jwtUtil;
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.succeed();
    }
    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody RegisterDTO registerDTO) {
        String token = userService.login(registerDTO);
        return Result.succeed(token);
    }
    @GetMapping("/profile")
    public Result<?> profile(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer", "");
        Long userId = jwtUtil.getUserId(token.trim());
        User user = userService.profile(userId);
        return Result.succeed(user);
    }
}