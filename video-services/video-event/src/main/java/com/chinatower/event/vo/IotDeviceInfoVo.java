package com.chinatower.event.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class IotDeviceInfoVo implements Serializable {

    /**
     * 物联设备id
     */
    private Long iotDeviceId;

    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备状态
     */
    private int deviceStatus;

    /**
     * 省编码
     */
    private String provinceId;
    /**
     * 省父编码
     */
    private String provinceParentId;
    /**
     * 省名称
     */
    private String provinceName;

    /**
     * 市编码
     */
    private String cityId;
    /**
     * 市父编码
     */
    private String cityParentId;
    /**
     * 市名称
     */
    private String cityName;

    /**
     * 区编码
     */
    private String countyId;
    /**
     * 区父编码
     */
    private String countyParentId;
    /**
     * 区名称
     */
    private String countyName;
}