package com.xxw.coedit.common.handler;
import com.xxw.coedit.common.exceptions.BizException;
import com.xxw.coedit.common.result.Result;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 JWT Token 过期异常
     * @param e ExpiredJwtException 异常对象
     * @return 统一响应结果，状态码 401
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public Result<?> handleTokenExpired(ExpiredJwtException e) {
        return Result.fail(401, "Token 已过期，请重新登录");
    }

    /**
     * 统一处理 JWT 相关异常
     * @param e JWT 异常对象（由 JJWT 在解析 Token 时抛出）
     * @return 统一返回结果，包含 401 状态码及错误信息
     */
    @ExceptionHandler(JwtException.class)
    public Result<?> handleJWTException(JwtException e) {
        return Result.fail(401, "Token 非法");
    }

    /**
     * 处理业务异常（BizException）
     * @param e BizException 业务异常对象
     * @return 统一响应结果，包含业务错误码和提示
     */
    @ExceptionHandler(BizException.class)
    public Result<?> handleBizException(BizException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 兜底异常处理（系统级异常）
     * @param e Exception 异常对象
     * @return 统一响应结果，状态码 500
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handle(Exception e) {
        return Result.fail(500, e.getMessage());
    }
}
