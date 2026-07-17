package com.xxw.coedit.controller;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxw.coedit.dto.request.ShareCreateDTO;
import com.xxw.coedit.security.WebUtils;
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
     * 分享文件给其他用户
     *
     * @param fileId         文件ID
     * @param shareCreateDTO 分享参数（目标用户、权限级别）
     * @param request        HTTP 请求，用于获取当前用户ID
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
     *
     * @param sharedId 分享记录ID
     * @param request  HTTP 请求，用于获取当前用户ID
     * @return 取消结果
     */
    @DeleteMapping("/{sharedId}")
    public Result<?> unShare(@PathVariable Long sharedId, HttpServletRequest request) {
        shareService.unShareFile(sharedId, webUtils.currentUserId(request));
        return Result.succeed("取消分享成功");
    }

    /**
     * 查询某文件的分享列表（我分享出去的）
     *
     * @param fileId  文件ID
     * @param request HTTP 请求，用于获取当前用户ID
     * @return 分享记录列表
     */
    @GetMapping("/file/{fileId}")
    public Result<?> listShares(@PathVariable Long fileId, HttpServletRequest request) {
        return Result.succeed(shareService.listSharesByFile(fileId, webUtils.currentUserId(request)));
    }

    /**
     * 分页查询我收到的分享文件
     *
     * @param current 当前页（默认第1页）
     * @param size    每页条数（默认5条）
     * @param request HTTP 请求，用于获取当前用户ID
     * @return 分页后的分享文件视图列表
     */
    @GetMapping("/received")
    public Result<?> recevied(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "5") Long size,
            HttpServletRequest request) {
        Page<SharedFileVO> voPage = new Page<>(current, size);
        Page<SharedFileVO> result = shareService.receivedPage(voPage, webUtils.currentUserId(request));
        return Result.succeed(result);
    }
}