package com.chinatower.bdm.modules.bmattendance.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class BmAttendanceLineBdmVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String attendanceId;
    private String tenantId;
    private String keeperId;
    private String keeperName;
    private String signTimeSegment;
    private String signInStatus;
    private String signInAddTime;
    private String signInGrid;
    private String signInOutside;
    private String signInCoordinate;
    private String signInAddress;
    private String signOutStatus;
    private String signOutAddTime;
    private String signOutGrid;
    private String signOutOutside;
    private String signOutCoordinate;
    private String signOutAddress;
    private String attendanceDate;
    private String creator;
    private String modifier;
    private String createTime;
    private Integer deleteFlag;
    /**
     * 上班时间
     */
    private String signInTime;
    /**
     * 下班时间
     */
    private String signOutTime;
    /**
     * 签到开始时间
     */
    private String signBegin;
    /**
     * 签退开始时间
     */
    private String signEnd;
    /**
     * 上班时间
     */
    private String busInTime;
    /**
     * 下班时间
     */
    private String busOutTime;
    private String industryCode;
    private String appVerCode;
    private String weekDay;
    private String gridId;
}
