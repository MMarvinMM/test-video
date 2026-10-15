package com.chinatower.device.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EnergyDeviceAlarmQueryReq implements Serializable {

    private Integer pageNum;
    private Integer pageSize;
    private String alarmName;
    private String alarmLevel;
    private String alarmStatus;
    private String devCode;
    private String devName;
    private String devType;
    private String alarmType;
    private String businessType;
    private String startTime;
    private String endTime;
    private List<String> stationCodeList;
    private List<String> devTypeList;
}
