package com.chinatower.airline.constants;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


public enum DJINestTypeCode {
    // 定义大疆的型号枚举值
    TYPE_200("200"),
    TYPE_210("210"),
    TYPE_201("201"),
    TYPE_230("230");

    private final String code;

    DJINestTypeCode(String code) {
        this.code = code;
    }

    // 提供一个方法根据编码获取枚举
    public static DJINestTypeCode fromCode(String code) {
        for (DJINestTypeCode type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}