package com.chinatower.airline.enums;

import org.apache.poi.sl.usermodel.ShapeType;

import java.util.Objects;

/**
 * @author WLF
 * @description
 * @date 2025/2/14 10:06
 */
public enum AirMapEnum {
    POINT("1", "标注点"),
    LINE("2", "标注线"),
    POLYGON("3", "标注多边形"),
    CIRCLE("4", "标注圆");

    private final String code;
    private final String name;

    // 构造函数
    AirMapEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    // 获取 code
    public String getCode() {
        return code;
    }

    // 获取 name
    public String getName() {
        return name;
    }

    // 根据 code 获取枚举值
    public static AirMapEnum getByCode(String code) {
        for (AirMapEnum type : AirMapEnum.values()) {
            if (Objects.equals(type.getCode(), code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
