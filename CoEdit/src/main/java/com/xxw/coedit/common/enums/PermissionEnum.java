package com.xxw.coedit.common.enums;
public enum PermissionEnum {
    NONE(0,"无权限"),
    READ_ONLY(1, "只读"),
    EDITABLE(2, "可编辑"),
    OWNER(3, "所有权限");
    private final int code;
    private final String desc;
    PermissionEnum(int code, String desc) { this.code = code; this.desc = desc; }
    public int getCode() { return code; }
    public String getDesc() { return desc; }
    public static PermissionEnum fromCode(Integer code) {
        if (code == null) return NONE;
        for (PermissionEnum e : values()) { if (e.code == code) return e; }
        return NONE;
    }
}