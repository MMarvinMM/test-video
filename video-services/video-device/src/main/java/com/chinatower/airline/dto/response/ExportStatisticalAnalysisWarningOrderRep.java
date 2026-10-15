package com.chinatower.airline.dto.response;

import lombok.Data;

@Data
public class ExportStatisticalAnalysisWarningOrderRep {
    private String flightRouteId;
    private String taskRecordId;
    private String warningTypeId;//告警类型
    private String alarmBody;//告警类型
}
