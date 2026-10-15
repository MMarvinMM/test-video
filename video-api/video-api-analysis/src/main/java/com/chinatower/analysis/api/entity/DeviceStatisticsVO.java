package com.chinatower.analysis.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 设备统计返回参数
 */
@Data
public class DeviceStatisticsVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 通道离线数量
     */
    private int channelCountOffline = 0;
    /**
     * 通道在线数量
     */
    private int channelCountOnline = 0;
    /**
     * 通道总数
     */
    private int channelCountTotal = 0;
    /**
     * 通道在线率
     */
    private String onlinePercentage = "0";
    /**
     * 设备离线数量
     */
    private int devCountOffline = 0;
    /**
     * 设备在线数量
     */
    private int devCountOnline = 0;
    /**
     * 设备总数
     */
    private int deviceCountTotal = 0;
    /**
     * 设备在线率
     */
    private String onlinePercentageDev = "0";

    /**
     * 设备从未连接数量
     */
    private int deviceNoConnectCount = 0;

    /**
     * 通道配置算法任务数量
     */
    private int disAlgorithmTaskNum = 0;
    /**
     * 通道启用算法任务数量
     */
    private int upAlgorithmTaskNum = 0;
    /**
     * 订购摄像机数量
     */
    private int orderDeviceNum = 0;

    /**
     * 开启告警定位摄像机数量
     */
    private int alarmLocationDevNum = 0;




}
