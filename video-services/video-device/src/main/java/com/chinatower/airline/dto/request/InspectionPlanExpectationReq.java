package com.chinatower.airline.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2024/12/12 11:07
 */
@Data
public class InspectionPlanExpectationReq {

    /**
     * 预计执行时间（首个架次飞行时间）
     */
    private LocalTime estimatedExecTime;

    /**
     * 预计执行日期列表
     */
    private List<LocalDate> estimatedExecDates;
}
