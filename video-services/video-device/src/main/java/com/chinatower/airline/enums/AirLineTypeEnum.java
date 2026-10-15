package com.chinatower.airline.enums;

/**
 * @author WLF
 * @description
 * @date 2024/11/7 9:43
 */

public enum AirLineTypeEnum {
    WAYPOINT(1, "航点航线"), ORTHOIMAGE(2, "正射影像"), TILTSHOTS(3, "倾斜摄影"), CIRCULARFLIGHT(4, "环绕飞行");

    private final int type;
    private final String description;

    AirLineTypeEnum(int type, String description) {
        this.type = type;
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    // 根据 type 获取对应的枚举实例
    public static AirLineTypeEnum fromType(int type) {
        for (AirLineTypeEnum flightType : AirLineTypeEnum.values()) {
            if (flightType.getType() == type) {
                return flightType;
            }
        }
        throw new IllegalArgumentException("Unknown flight type: " + type);
    }

    // 根据 type 获取对应的 typename
    public static String getTypeName(int type) {
        AirLineTypeEnum flightType = fromType(type);
        return flightType.getDescription();
    }

    // 生成名称的方法
    public static String generateName(int type, String count) {
        String typeName = getTypeName(type);
        return typeName + count;
    }
}
