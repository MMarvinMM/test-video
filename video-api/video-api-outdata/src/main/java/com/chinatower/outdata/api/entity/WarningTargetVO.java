package com.chinatower.outdata.api.entity;

import lombok.Data;

@Data
public class WarningTargetVO {
    // 告警时间 格式yyyy-MM-dd HH:mm:ss
    private String alarmTime;
    // 告警编码
    private String alarmCode;
    // 任务编码
    private String taskId;
    // 目标编码
    private String targetId;
    // 客户编码
    private String tenantId;
    // 行业编码
    private String industryCode;
}
