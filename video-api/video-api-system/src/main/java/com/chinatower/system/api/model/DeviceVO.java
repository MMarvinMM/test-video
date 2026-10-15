package com.chinatower.system.api.model;

import lombok.Data;

/**
 * @desription: 设备
 * @data 创建时间：2023/8/22 9:58
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class DeviceVO {

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备状态:0在线，1离线
     */
    private Integer deviceStatus;

    /**
     * 设备类型
     */
    private String deviceType;


    /**
     * 设备类型
     */
    private String iotDeviceType;

    /**
     * 省
     */
    private String provinceId;

    /**
     * 城市
     */
    private String  cityId;

    /**
     * 街道
     */
    private String townCode;

    /**
     * 区县
     */
    private String countyId;


}
