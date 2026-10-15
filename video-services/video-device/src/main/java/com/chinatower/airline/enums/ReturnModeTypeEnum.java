package com.chinatower.airline.enums;

import lombok.Getter;

/**
 * 返航类型枚举
 *
 * @author ljy
 * @date 2026/1/4
 */
@Getter
public enum ReturnModeTypeEnum {
    /**
     * 0-直线返航；1-原路返航；2-原地降落
     */
    LINE(0, "直线返航"),
    ORIGINAL(1, "原路返航"),
    LANDING(2, "原地降落");
    
    private final int code;
    
    private final String desc;
    
    ReturnModeTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static boolean isValid(int code) {
        for (ReturnModeTypeEnum value : ReturnModeTypeEnum.values()) {
            if (value.code == code) {
                return true;
            }
        }
        return false;
    }
    
    
}
