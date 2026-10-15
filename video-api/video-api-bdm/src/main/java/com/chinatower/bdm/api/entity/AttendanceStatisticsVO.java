package com.chinatower.bdm.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 考勤统计
 */
@Data
public class AttendanceStatisticsVO extends AttendanceStatisticsParam implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 正常个数
     */
    private String normalCount;
    /**
     * 异常个数
     */
    private String dbnormalCount;

    /**
     * 考勤明细
     */
    private List<AttendanceStatisticsDetailVO> list;


}
