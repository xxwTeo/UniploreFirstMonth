package com.xxw.coedit.common.result;
import lombok.Data;
@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;
    public static <T> Result<T> succeed() { Result<T> r = new Result<>(); r.code = 1; return r; }
    public static <T> Result<T> succeed(T data) { Result<T> r = new Result<>(); r.code = 1; r.data = data; return r; }
    public static <T> Result<T> fail(int code,String msg) { Result<T> r = new Result<>(); r.code = 0; r.message = msg; return r; }
    public static <T> Result<T> error(String msg) { return fail(0, msg); }
}