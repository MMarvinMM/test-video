package com.chinatower.sync.consumer.entity;

import lombok.Data;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2025/7/18 17:03
 */
@Data
public class PointsCompareTask {
    private String id;
    private String isOpen;          // "0" 或 "1"
    private String compareStrategy; // "1" 或 "2"
    private List<String> algorithm; // 算法列表（如 ["ZGTT-1616102001", ...]）
    private String algorithmCode;
    private List<String> executionDate; // 执行日期列表（如 ["2025-07-24", ...]）
    private String executionStartDate;
    private String executionEndDate;
    private String compareDate;     // 对比日期
    private Double lng;            // 经度
    private Double lat;            // 纬度
    private String flightRouteId;           //
    private String userId;
    private String lastCycleDate;
    private String taskRecordId;
}
