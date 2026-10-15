package com.chinatower.industry.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class IndustryDeviceStatisticsVO implements Serializable {

    /**
     * 摄像机设备订购数量
     */
    private Integer deviceNum;

    /**
     * 物联设备订购数量
     */
    private Integer sensorDeviceNum;

    /**
     * 雷达设备订购数量
     */
    private Integer radarDeviceNum;

    /**
     * 无人机设备订购数量
     */
    private Integer uavDeviceNum;

    /**
     * 喇叭设备订购数量
     */
    private Integer hornDeviceNum;

    /**
     * 执法记录仪设备订购数量
     */
    private Integer recorderDeviceNum;
}
