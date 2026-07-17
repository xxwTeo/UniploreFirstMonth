package com.xxw.coedit.common.enums;

import lombok.Getter;

/**
 * 统一管理异常
 */
@Getter
public enum ErrorCode {

    // ===== 用户认证 =====
    USER_NOT_FOUND(10001, "用户不存在"),
    USERNAME_OR_PASSWORD_ERROR(10002, "用户名或密码错误"),
    USERNAME_ALREADY_EXISTS(10003, "用户名已存在"),
    AUTH_TOKEN_INVALID(10004, "未登录"),
    TOKEN_INVALID(10005, "Token 非法"),
    TOKEN_EXPIRED(10006, "Token 已过期"),

    // ===== 分享 =====
    SHARE_TARGET_NOT_FOUND(20001, "分享目标用户不存在"),
    SHARE_FILE_NOT_FOUND(20002, "文件不存在或无权限"),
    SHARE_TO_SELF(20003, "不能分享给自己"),
    SHARE_NOT_EXIST(20004, "分享记录不存在"),

    // ===== 协同编辑文件权限 =====,
    FILE_IN_CO_EDIT(30001, "文件正在协同编辑中，请先进入协同模式"),
    CO_EDIT_SESSION_EXPIRED(30002, "协同编辑会话已过期，请重新进入编辑"),


    // ===== 文件权限 =====
    SHARE_NO_PERMISSION(20005, "无权限取消分享"),
    FILE_NOT_FOUND(40001, "文件不存在"),
    NO_VIEW_PERMISSION(40002, "无权限查看文件"),
    NO_EDIT_PERMISSION(40003, "无权限编辑文件"),
    NO_DELETE_PERMISSION(40004, "无权限删除文件");


    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
