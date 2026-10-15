package com.chinatower.platform.consumer.item;

import lombok.Data;

import java.io.Serializable;

@Data
public class WarningTargetRadarTemplateDTO implements Serializable {
    // 任务编码
    private String taskId;
    // 客户编码
    private String tenantId;
    // 行业编码
    private String industryCode;
}
