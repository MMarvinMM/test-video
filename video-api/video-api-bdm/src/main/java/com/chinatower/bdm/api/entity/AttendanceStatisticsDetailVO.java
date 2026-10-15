package com.chinatower.bdm.api.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class AttendanceStatisticsDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 考勤日期
     */
    private String date;
    /**
     * 考勤状态
     */
    private String status;
}
