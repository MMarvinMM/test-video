package com.chinatower.device.entity.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class UavMonitorGeoJson {
    /**
     * 电子围栏面融合
     */
    private JSONObject geometriesBuffer;
    /**
     * 电子围栏线融合
     */
    private JSONObject geometries;
    /**
     * 航线id
     */
    private String flightRoutId;
    /**
     * 镜头大小 【】
     */
    private String sensorSize;
    /**
     * 飞机类型 0大疆 1不是大疆
     */
    private String uavType;
    /**
     * 电子围栏面融合
     */
    private String geometriesBufferStr;
    /**
     * 电子围栏线融合
     */
    private String geometriesStr;
}
