package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceComprehensiveVO implements Serializable {

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

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

    /**
     * 站址编码
     */
    private String siteCode;

    /**
     * 设备状态
     */
    private String deviceStatus;

    /**
     * 设备状态名称
     */
    private String deviceOriginalStatus;

    /**
     * 摄像机自定义图标
     */
    private String imageUrl;
}
