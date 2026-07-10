package com.xxw.coedit.controller;

import com.xxw.coedit.security.WebUtils;
import com.xxw.coedit.entity.File;
import com.xxw.coedit.service.FileService;
import com.xxw.coedit.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final WebUtils webUtils;

    /**
     * 创建文本文件
     * @param name    文件名（必传）
     * @param content 文件内容（可选，默认为空字符串）
     * @param request HTTP 请求对象，用于从 Header 中获取 token 解析当前登录用户
     * @return 创建成功的文件信息
     */
    @PostMapping
    public Result<?> create(@RequestParam String name,
                            @RequestParam(defaultValue = " ") String content,
                            HttpServletRequest request) {
        File file = fileService.createFile(name, content, webUtils.currentUserId(request));
        return Result.succeed(file);
    }

    /**
     * 获取文件详情
     * @param id 文件ID
     * @param request HTTP 请求（用于解析用户身份）
     * @return 文件详情
     */
    @GetMapping("/{id}")
    public Result<?> getFile(@PathVariable Long id, HttpServletRequest request) {
        return Result.succeed(fileService.getFile(id, webUtils.currentUserId(request)));
    }

    /**
     * 删除相应的文件和对应分享内容
     * @param id 被删除的文件 id
     * @param request 获取 token ,确定 userId
     * @return 删除情况
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteFile(@PathVariable Long id, HttpServletRequest request) {
        boolean deleted = fileService.deleteFile(id, webUtils.currentUserId(request));
        if (!deleted) {
            return Result.error("删除失败");
        }
        return Result.succeed("已删除");
    }

    /**
     * 更新文件内容（带权限校验）
     * @param id 文件ID
     * @param content 新的文件内容
     * @param request  从 token 中获取用户id
     * @return 更新后的文件实体
     */
    @PutMapping("/{id}")
    public Result<?> updateFile(@PathVariable Long id,
                                @RequestParam String content,
                                HttpServletRequest request) {
        File file = fileService.updateFile(id, content, webUtils.currentUserId(request));
        return Result.succeed(file);
    }

    /**
     * 查询当前用户拥有的所有文件
     * @param request  从 token 中获取用户id
     * @return  该用户的文件集合
     */
    @GetMapping("/my")
    public Result<?> myFiles(HttpServletRequest request) {
        List<File> fileList = fileService.myFiles(webUtils.currentUserId(request));
        return Result.succeed(fileList);
    }
}
