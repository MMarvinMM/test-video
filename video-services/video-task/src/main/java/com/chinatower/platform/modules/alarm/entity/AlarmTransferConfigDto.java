package com.chinatower.platform.modules.alarm.entity;

import lombok.Data;

/**
 * 告警转移配置实体类
 */
@Data
public class AlarmTransferConfigDto {

    /**
     * 告警来源 （1-AI告警； 2-人工上报；3-一键告警；4-摄像机告警；8-物联告警；9-卫星告警；10-雷达告警）
     */
    private String warningSource;

    /**
     * 告警类型
     */
    private String alarmType;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 告警转移策略名称
     */
    private String alarmTransferName;

    /**
     * 是否从未处理 （1：是  0：否）
     */
    private String neverProcessFlag;

    /**
     * 事件状态 （1待研判 、2待调度、4待处置、5待核实、6已完结、9待受理）
     */
    private String orderStatus;

    /**
     * 告警转移时间 （0：每天 1：每月）
     */
    private String alarmTransferTime;

    /**
     * 日期 （1-31正整数）
     */
    private String transferDate;

    /**
     * 转移时间间隔（单位天，1-366正整数）
     */
    private Integer transferInterval;

    /**
     * 行业编码
     */
    private String industryCode;

}
