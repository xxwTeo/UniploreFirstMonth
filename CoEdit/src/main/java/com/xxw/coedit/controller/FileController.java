package com.xxw.coedit.controller;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxw.coedit.entity.File;
import com.xxw.coedit.security.SessionAccessGate;
import com.xxw.coedit.security.WebUtils;
import com.xxw.coedit.service.EditSessionService;
import com.xxw.coedit.service.FileService;
import com.xxw.coedit.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor

/**
 * 文件资源管理控制器
 * 负责文件相关的 HTTP 接口，包括 CRUD、编辑锁、在线用户管理等
 */
public class FileController {

    private final FileService fileService;
    private final WebUtils webUtils;
    private final EditSessionService editSessionService;
    private final SessionAccessGate sessionAccessGate;

    /**
     * 创建新文件
     *
     * @param body    请求体，包含文件名和内容
     * @param request HTTP 请求对象，用于获取当前登录用户
     * @return 创建成功的文件信息
     */
    @PostMapping
    public Result<?> create(@RequestBody Map<String, String> body,
                            HttpServletRequest request) {
        String name = body.get("name");
        String content = body.getOrDefault("content", "");
        File file = fileService.createFile(name, content, webUtils.currentUserId(request));
        return Result.succeed(file);
    }

    /**
     * 根据文件 ID 查询文件详情
     *
     * @param id      文件 ID
     * @param request HTTP 请求对象，用于获取当前登录用户
     * @return 文件详细信息
     */
    @GetMapping("/{id}")
    public Result<?> getFile(@PathVariable Long id, HttpServletRequest request) {
        return Result.succeed(fileService.getFile(id, webUtils.currentUserId(request)));
    }

    /**
     * 删除指定文件
     *
     * @param id      文件 ID
     * @param request HTTP 请求对象，用于获取当前登录用户
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteFile(@PathVariable Long id,
                                @RequestHeader(value = "X-Session-Id", required = false) String sessionId,
                                HttpServletRequest request) {
        Long uid = webUtils.currentUserId(request);
        sessionAccessGate.ensureEditable(id, uid, sessionId);
        boolean deleted = fileService.deleteFile(id, uid);
        if (!deleted) return Result.error("删除失败");
        return Result.succeed("已删除");
    }


    /**
     * 更新文件内容
     *
     * @param id      文件 ID
     * @param body    请求体，包含新的文件内容
     * @param request HTTP 请求对象，用于获取当前登录用户
     * @return 更新后的文件信息
     */
    @PutMapping("/{id}")
    public Result<?> updateFile(@PathVariable Long id,
                                @RequestBody Map<String, String> body,
                                @RequestHeader(value = "X-Session-Id", required = false) String sessionId,
                                HttpServletRequest request) {
        String content = body.get("content");
        File file = fileService.updateFile(id, content, webUtils.currentUserId(request), sessionId);
        return Result.succeed(file);
    }

    /**
     * 查询当前登录用户的文件列表（分页）
     *
     * @param current 当前页，默认第 1 页
     * @param size    每页条数，默认 50 条
     * @param request HTTP 请求对象，用于获取当前登录用户
     * @return 分页后的文件列表
     */
    @GetMapping("/my")
    public Result<?> myFiles(@RequestParam(defaultValue = "1") Long current,
                              @RequestParam(defaultValue = "50") Long size,
                              HttpServletRequest request) {
        Page<File> page = new Page<>(current, size);
        return Result.succeed(fileService.myFiles(page, webUtils.currentUserId(request)));
    }


    // ===================== 在线用户相关 =====================

    /**
     * 查询正在编辑该文件的在线用户列表
     *
     * @param id 文件 ID
     * @return 在线编辑用户列表
     */
    @GetMapping("/{id}/online")
    public Result<?> onlineUsers(@PathVariable Long id) {
        return Result.succeed(editSessionService.listByFiledId(id));
    }
}