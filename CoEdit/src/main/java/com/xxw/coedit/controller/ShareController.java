package com.xxw.coedit.controller;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxw.coedit.security.WebUtils;
import com.xxw.coedit.common.enums.PermissionEnum;
import com.xxw.coedit.service.impl.ShareServiceImpl;
import com.xxw.coedit.common.result.Result;
import com.xxw.coedit.dto.response.SharedFileVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/shares")
@RequiredArgsConstructor
public class ShareController {
    private final ShareServiceImpl shareService;
    private final WebUtils webUtils;
    @PostMapping
    public Result<?> share(@RequestParam Long fileId,
                            @RequestParam Long targetUserId,
                            @RequestParam PermissionEnum perm,
                            HttpServletRequest request) {
        shareService.shareFile(fileId, targetUserId, perm, webUtils.currentUserId(request));
        return Result.succeed("分享成功");
    }
    @DeleteMapping("/{sharedId}")
    public Result<?> unShare(@PathVariable Long sharedId, HttpServletRequest request) {
        shareService.unShareFile(sharedId, webUtils.currentUserId(request));
        return Result.succeed("取消分享成功");
    }
    @GetMapping("/received")
    public Result<?> recevied(@RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "5") Long size,
            HttpServletRequest request) {
        Page<SharedFileVO> voPage = new Page<>(current, size);
        Page<SharedFileVO> result = shareService.receviedPage(voPage, webUtils.currentUserId(request));
        return Result.succeed(result);
    }
}