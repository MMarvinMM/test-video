package com.chinatower.gis.api.entity;

import lombok.Data;

@Data
public class LonLat {

    /**
     * 经纬度
     */
    private String lon;
    /**
     * 维度
     */
    private String lat;
    /**
     * 设备编码
     */
    private String deviceId;
}
