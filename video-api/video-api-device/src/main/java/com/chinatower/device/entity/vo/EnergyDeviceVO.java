package com.chinatower.device.entity.vo;

import lombok.Data;

@Data
public class EnergyDeviceVO {

    private String devCode;
    private String devName;
    private String devType;
    private String businessType;
    private String onlineStatus;
    private String provinceId;
    private String provinceName;
    private String cityId;
    private String cityName;
    private String countyId;
    private String countyName;
    private String stationCode;
    private String stationName;
    private String createTime;
    private String address;
    private String lat;
    private String lon;
    private String factory;
}
