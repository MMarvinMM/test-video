package com.chinatower.device.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EnergyDeviceAlarmStatisticsQueryReq implements Serializable {

    private List<String> stationCodeList;

    private String startTime;

    private String endTime;

    private String businessType;

    private String alarmStatus;
}
