package com.chinatower.receive.constant;

/**
 * 用户类型枚举
 * @author chmod
 * @version 1.0
 * @date 2022/1/27 14:27
 */
public enum UserTypeConstant {
    USER("1", "用户"),
    ROLE("2", "角色"),
    LEADER("3", "领导");

    private final String type;
    private final String typeName;

    public String getType() {
        return this.type;
    }

    public String getTypeName() {
        return this.typeName;
    }

    UserTypeConstant(String type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }
}
