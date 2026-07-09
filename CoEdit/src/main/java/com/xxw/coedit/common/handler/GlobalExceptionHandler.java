package com.xxw.coedit.common.handler;
import com.xxw.coedit.common.exceptions.BizException;
import com.xxw.coedit.common.result.Result;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ExpiredJwtException.class)
    public Result<?> handleTokenExpired(ExpiredJwtException e) { return Result.fail(401, "Token已过期，请重新登录"); }
    @ExceptionHandler(BizException.class)
    public Result<?> handleBizException(BizException e) { return Result.fail(e.getCode(), e.getMessage()); }
    @ExceptionHandler(Exception.class)
    public Result<?> handle(Exception e) { return Result.fail(500, e.getMessage()); }
}