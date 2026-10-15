package com.chinatower.airline.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ExportStatisticalAnalysisTaskRep {
    private String flightRouteId;
    private String planId;
    private String planTaskId;
    private String deviceName;
    private String uavCode;
    private String flyStartTime;
    private String flyEndTime;
    private String planName;
    private String taskStatus;
    private String realityTime;
    private String flightMainType;
    private String flightRouteName;
    private Integer flyCount;
    private Double flyTime;
    private Double accumulateMileage;

    //key 告警类型 value 告警数量
//    private Map<String, Integer> algorithmInfoMap;
    private String algorithmInfoMap;
}
