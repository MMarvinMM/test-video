package com.chinatower.platform.entity.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderRadarDeviceVO implements Serializable {

    //订购关系编码
    private String orderCode;

    //设备编码
    private String deviceCode;

    //设备名称
    private String deviceName;

    //站址编码
    private String siteCode;

    //站址名称
    private String siteName;

    //站址地址
    private String location;

    //经度
    private String longitude;
    //纬度
    private String latitude;

}
