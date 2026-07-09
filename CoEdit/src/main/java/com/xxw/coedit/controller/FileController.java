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
    @PostMapping
    public Result<?> create(@RequestParam String name,
                            @RequestParam(defaultValue = " ") String content,
                            HttpServletRequest request) {
        File file = fileService.createFile(name, content, webUtils.currentUserId(request));
        return Result.succeed(file);
    }
    @GetMapping("/{id}")
    public Result<?> getFile(@PathVariable Long id, HttpServletRequest request) {
        return Result.succeed(fileService.getFile(id, webUtils.currentUserId(request)));
    }
    @DeleteMapping("/{id}")
    public Result<?> deleteFile(@PathVariable Long id, HttpServletRequest request) {
        boolean deleted = fileService.deleteFile(id, webUtils.currentUserId(request));
        if (!deleted) {
            return Result.error("删除失败");
        }
        return Result.succeed("已删除");
    }
    @PutMapping("/{id}")
    public Result<?> updateFile(@PathVariable Long id,
                                @RequestParam String content,
                                HttpServletRequest request) {
        File file = fileService.updateFile(id, content, webUtils.currentUserId(request));
        return Result.succeed(file);
    }
    @GetMapping("/my")
    public Result<?> myFiles(HttpServletRequest request) {
        List<File> fileList = fileService.myFiles(webUtils.currentUserId(request));
        return Result.succeed(fileList);
    }
}