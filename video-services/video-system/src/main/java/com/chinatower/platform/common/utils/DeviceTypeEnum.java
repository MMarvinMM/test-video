package com.chinatower.platform.common.utils;


public enum DeviceTypeEnum {
    SENSOR(8, "物联设备"),
    SATELLITE(9, "卫星设备"),
    RADAR(10, "雷达设备"),
    UAV(11, "无人机设备"),
    HORN(12, "大喇叭设备"),
    LER(13, "执法记录仪设备"),

    NEST(15, "无人机机场"),
    LAYER(14, "图层");



    private final Integer type;
    private final String typeName;

    public Integer getType() {
        return this.type;
    }

    public String getTypeName() {
        return this.typeName;
    }

    DeviceTypeEnum(Integer type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }
}
