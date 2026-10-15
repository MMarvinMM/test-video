package com.chinatower.receive.vo.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @program: VideoPlatformGit
 * @description: 模糊查询设备信息
 * @author: hued@strongdata.com.cn
 * @create: 2021/05/13 20:25
 */
@Data
@ToString
public class DeviceSiteReceiveVO {

    private String devName;

    // 设备编号
    private String deviceCode;

    // 设备名称
    private String deviceName;

    private String status;

    private String longitude;

    private String latitude;

    private String location;

    private String height;

    private String siteCode;

    private String provinceId;
    private String cityId;
    private String countyId;
    private String provinceName;
    private String cityName;
    private String countyName;

    private String modelCode;
    private String modelId;
    private String deviceModelCode;
    private String focusRange;
    private String vertiDownNeg;
    /**
     * 告警定位（1:开启 0关闭）
     */
    private String alarmLocation;
    private String modelName;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 摄像机图标地址
     * */
    private String deviceImageUrl;

    /**
     * 摄像机类型
     */
    private String deviceType;
    /**
     * 摄像机类型名称
     */
    private String deviceTypeName;

    /**
     * 摄像机子类型
     */
    private String deviceSubType;
    /**
     * 摄像机子类型名称
     */
    private String deviceSubTypeName;
}
