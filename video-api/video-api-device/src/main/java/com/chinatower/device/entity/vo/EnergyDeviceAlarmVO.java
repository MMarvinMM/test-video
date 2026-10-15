package com.chinatower.device.entity.vo;

import lombok.Data;

@Data
public class EnergyDeviceAlarmVO {

    private String alarmName;
    private String alarmLevel;
    private String alarmStatus;
    private String alarmType;
    private String alarmDesc;
    private String businessType;
    private String provinceId;
    private String provinceName;
    private String cityId;
    private String cityName;
    private String countyId;
    private String countyName;
    private String devCode;
    private String devName;
    private String devType;
    private String mid;
    private String midValue;
    private String reportDevCode;
    private String reportDevName;
    private String firstAlarmTime;
}
