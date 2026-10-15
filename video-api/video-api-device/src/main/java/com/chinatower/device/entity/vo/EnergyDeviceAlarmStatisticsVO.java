package com.chinatower.device.entity.vo;

import lombok.Data;


@Data
public class EnergyDeviceAlarmStatisticsVO {

    private Long alarmTotal;
    private Long alarmProgress;
    private Long alarmCompleted;
}
