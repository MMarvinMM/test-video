package com.chinatower.event.entity.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @program: VideoPlatformGit
 * @description: 模糊查询设备信息
 * @author: daiyong
 * @create: 2022/09/15
 */
@Data
@ToString
public class DeviceSiteInfoVO {

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
}
