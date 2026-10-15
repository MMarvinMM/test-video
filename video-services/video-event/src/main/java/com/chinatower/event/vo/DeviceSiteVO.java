package com.chinatower.event.vo;

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
public class DeviceSiteVO {

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
     * 通道名称
     */
    private String channelName;

    /**
     * 摄像机图标地址
     * */
    private String deviceImageUrl;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 设备类型
     * @required true
     */
    private String deviceType;

    /**
     * 设备类型名称
     * @required true
     */
    private String deviceTypeName;

}
