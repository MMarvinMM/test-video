package com.chinatower.event.entity.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * @ClassName AlarmOrderByDeviceDTO
 * @Deacription
 * @Author chenmf
 * @Date 2022/3/10 19:24
 * @Version 1.0
 **/
@Data
public class AlarmOrderByDeviceDTO implements Serializable {

    /**
     * 设备编码
     * @required true
     */
    private String deviceCode;

    /**
     * 通道编码
     * @required true
     */
    private String channelCode;

    /**
     * 站址地址
     * @required true
     */
    private String location;

    /**
     * 设备厂家code
     * @required true
     */
    private String modelCode;

    /**
     * 经度
     * @required true
     */
    private String longitude;

    /**
     * 纬度
     * @required true
     */
    private String latitude;

    /**
     * 设备挂高，单位：米
     * @required true
     */
    private String height;
}
