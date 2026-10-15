package com.chinatower.device.dto;

import lombok.Data;

@Data
public class AlgorithmHornInfoVO {

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备厂家编码
     */
    private String manufacturerCode;

    /**
     * 设备厂家名称
     */
    private String manufacturerName;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 设备类型名称
     */
    private String deviceTypeName;

    /**
     * 设备挂高
     */
    private String height;

    /**
     * 设备地址
     */
    private String location;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    private String provinceCode;

    private String provinceName;

    private String cityCode;

    private String cityName;

    private String countyCode;

    private String countyName;

    private String townCode;

    private String townName;

    private String accessNode;
}
