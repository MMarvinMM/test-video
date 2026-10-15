package com.chinatower.airline.enums;

import lombok.Getter;

/**
 * 操作类型枚举
 *
 * @author ljy
 * @date 2025/12/30
 */
@Getter
public enum OptTypeEnum {
    /**
     * 0：取消；1：获取
     */
    CANCEL(0, "取消"),
    GET(1, "获取");
    
    private final int code;
    
    private final String desc;
    
    OptTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static boolean isValid(int code) {
        for (OptTypeEnum value : OptTypeEnum.values()) {
            if (value.code == code) {
                return true;
            }
        }
        return false;
    }
}
