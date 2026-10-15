package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RadarDeviceRelVO implements Serializable {

    private String tenantId; //租户编码
    private String tenantName; //租户名称

    private String deviceCode; //设备编码
    private String deviceName; //设备名称
    private String location; //设备地址
    private String modelId;
    private String modelName;
    private String longitude; //经度
    private String latitude; //纬度

    private String height;
    /**
     * 设备型号
     */
    private String deviceModelCode;
}
