package com.chinatower.platform.modules.statistics.entity;

import lombok.Data;

@Data
public class StatisticalAnalysisWarningOrderVO {
    private String taskRecordId;
    private String warningTypeId;//告警类型
    private String alarmBody;//告警类型
    private String alarmNum;//告警数量
}
