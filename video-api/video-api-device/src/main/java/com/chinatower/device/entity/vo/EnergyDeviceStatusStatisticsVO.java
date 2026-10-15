package com.chinatower.device.entity.vo;

import lombok.Data;


@Data
public class EnergyDeviceStatusStatisticsVO {

    private String stationCode;
    private String stationName;
    private Integer offlineNum;
    private Integer onlineNum;
    private Integer totalNum;
    private Integer noConnectedNum;
}
