package com.chinatower.platform.common.utils;

/**
 * 用户类型枚举
 * @author chmod
 * @version 1.0
 * @date 2022/1/27 14:27
 */
public enum UserTypeEnum {
    TANENT("2", "租户"),
    TOWER("0", "铁塔");

    private final String type;
    private final String typeName;

    public String getType() {
        return this.type;
    }

    public String getTypeName() {
        return this.typeName;
    }

    UserTypeEnum(String type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }
}
