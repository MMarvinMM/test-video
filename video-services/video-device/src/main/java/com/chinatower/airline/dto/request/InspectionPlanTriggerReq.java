package com.chinatower.airline.dto.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2024/12/12 11:19
 */
@Data
public class InspectionPlanTriggerReq {
    private LocalDateTime regularExecutionDate;
    private Integer planType;
    private LocalTime cycleExecutionTime;
    private Integer cycleExecutionUnit;
    /**
     * 日历值
     * 周期执行以周为维度时，该值只支持周一（1）到周天（7）；
     * 周期执行以月为维度时，该值只支持1号到31号
     */
    private List<Integer> intervalValues;
    /**
     * 计划开始时间
     * 仅plan_type=3时有用
     */
    private LocalDateTime startTime;
    /**
     * 每日执行间隔
     * 间隔xx天
     */
    private Integer dayInterval;
    /**
     * 计划截止时间
     */
    private LocalDateTime endTime;
}
