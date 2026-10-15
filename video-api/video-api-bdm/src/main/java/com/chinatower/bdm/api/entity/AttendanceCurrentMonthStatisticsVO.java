package com.chinatower.bdm.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 考勤统计
 */
@Data
public class AttendanceCurrentMonthStatisticsVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 出勤（天）
     */
    private Integer normalCount;
    /**
     * 迟到/早退（次）
     */
    private Integer abnormalCount;

    /**
     * 缺卡（次）
     */
    private Integer lackCount;


}
