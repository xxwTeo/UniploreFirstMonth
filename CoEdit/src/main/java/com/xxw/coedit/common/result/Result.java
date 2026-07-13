package com.xxw.coedit.common.result;

import lombok.Data;

@Data
public class Result<T> {
    private int code;   //编码：1成功，0失败
    private String message;     //错误信息
    private T data;     //数据

    public static <T> Result<T> succeed() {
        Result<T> result = new Result<>();
        result.code = 1;
        return result;
    }

    public static <T> Result<T> succeed(T data) {
        Result<T> result = new Result<>();
        result.code = 1;
        result.data = data;
        return result;
    }

    public static <T> Result<T> fail(int code,String msg) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = msg;
        return result;
    }

        public static <T> Result<T> error(String msg) {
        return fail(0, msg);
    }
}
