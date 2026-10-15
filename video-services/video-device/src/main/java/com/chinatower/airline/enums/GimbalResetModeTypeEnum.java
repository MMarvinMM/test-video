package com.chinatower.airline.enums;

import lombok.Getter;

/**
 * 重置模式类型
 *
 * @author ljy
 * @date 2025/12/30
 */
@Getter
public enum GimbalResetModeTypeEnum {
    /**
     * 0-回中,1-向下,2-偏航回中,3-俯仰向下
     */
    
    RETURN_CENTER(0, "回中"),
    DOWN(1, "向下"),
    RETURN_YAW_CENTER(2, "偏航回中"),
    DOWN_PITCH(3, "俯仰向下");
    
    private final int code;
    
    private final String desc;
    
    GimbalResetModeTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static boolean isValid(int code) {
        for (GimbalResetModeTypeEnum value : GimbalResetModeTypeEnum.values()) {
            if (value.code == code) {
                return true;
            }
        }
        return false;
    }
    
}
