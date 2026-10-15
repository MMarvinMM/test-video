package com.chinatower.device.entity.dto;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2024/12/5 14:52
 */
@Data
public class TelemetryReq {
    /**
     * 告警时间
     */
    private String alarmTime;
    /**
     * 设备编码(优先机巢)
     */
    private String deviceCode;

    /**
     * 任务ID
     */
    private String taskRecordId;
}
