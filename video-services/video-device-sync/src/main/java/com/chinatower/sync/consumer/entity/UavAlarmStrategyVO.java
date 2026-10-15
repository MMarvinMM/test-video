package com.chinatower.sync.consumer.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UavAlarmStrategyVO implements Serializable {
    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 失联告警短信接收客户userId，逗号分隔
     */
    private String alarmUserId;
    /**
     * 告警延迟时间，时
     */
    private String alarmDelayHour;
    /**
     * 告警延迟时间，分
     */
    private String alarmDelayMinute;
    /**
     * 行业编码
     */
    private String industryCode;
    /**
     * 客户编码
     */
    private String tenantId;
    /**
     * 是否开启告警延迟，0：不开启；1：开启
     */
    private String delayOpen;
}
