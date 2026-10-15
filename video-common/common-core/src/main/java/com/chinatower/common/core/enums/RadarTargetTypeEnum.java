package com.chinatower.common.core.enums;

/**
 * 雷达目标类型枚举类
 */
public enum RadarTargetTypeEnum {


    RADAR_TARGET("RADAR","雷达目标"),

    AIS_A("AIS_A","A类AIS目标"),

    AIS_B("AIS_B","B类AIS目标"),

    RADAR_AIS_A("RADAR_AIS_A","雷达目标与A类AIS目标的融合"),

    RADAR_AIS_B("RADAR_AIS_B","雷达目标与B类AIS目标的融合"),

    BDS("BDS","北斗目标");


    /**
     * 目标类型
     */
    private String targetType;

    /**
     * 描述
     */
    private String targetName;

    RadarTargetTypeEnum(String targetType,String targetName){

        this.targetType = targetType;
        this.targetName = targetName;
    }


    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
}
