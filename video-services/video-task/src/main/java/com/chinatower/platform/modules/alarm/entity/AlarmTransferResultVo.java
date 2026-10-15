package com.chinatower.platform.modules.alarm.entity;

import lombok.Data;

/**
 * 告警转移返回结果实体
 */
@Data
public class AlarmTransferResultVo {

    /**
     * 告警编号
     */
    private String warningOrderId;

    /**
     * 告警时间
     */
    private String alarmTime;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 告警类型ID
     */
    private String warningTypeId;

    /**
     * 告警来源
     */
    private String warningSource;

    /**
     * 租户编码
     */
    private String tenantId;
}
