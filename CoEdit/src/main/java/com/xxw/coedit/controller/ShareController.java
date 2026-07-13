package com.xxw.coedit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxw.coedit.dto.request.ShareCreateDTO;
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

    /**
     * 分享文件接口
     * @param fileId        文件ID
     * @param shareCreateDTO       当前用户对文件的权限与目标文件
     * @param request       HTTP请求，用于获取当前登录用户信息
     * @return 分享结果
     */
    @PostMapping("/{fileId}/share")
    public Result<?> share(@PathVariable Long fileId,
                            @RequestBody ShareCreateDTO shareCreateDTO,
                            HttpServletRequest request) {
        shareService.shareFile(fileId,shareCreateDTO, webUtils.currentUserId(request));
        return Result.succeed("分享成功");
    }

    /**
     * 取消文件分享
     * 说明：
     * - sharedId 是分享记录的主键（file_shares.id）
     * - 不是文件ID，也不是被分享用户ID
     * - 只有文件拥有者才允许取消
     *
     * @param sharedId 分享记录ID
     * @param request  HTTP请求对象，用于获取当前登录用户ID
     * @return 取消分享成功提示
     */
    @DeleteMapping("/{sharedId}")
    public Result<?> unShare(@PathVariable Long sharedId, HttpServletRequest request) {
        shareService.unShareFile(sharedId, webUtils.currentUserId(request));
        return Result.succeed("取消分享成功");
    }

    /**
     * 查询当前用户收到的分享文件列表
     * @param request HTTP请求对象，用于解析当前登录用户身份信息
     * @return 统一返回结果，包含当前用户收到的分享文件列表
     */
    @GetMapping("/received")
    public Result<?> recevied(@RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "5") Long size,
            HttpServletRequest request) {
        Page<SharedFileVO> voPage = new Page<>(current, size);
        Page<SharedFileVO> result = shareService.receivedPage(voPage, webUtils.currentUserId(request));
        return Result.succeed(result);
    }
}
