package com.chinatower.bdm.api.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class BmAttendanceLineVO implements Serializable {
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
    private String signInTime;
    private String signOutTime;
    private String signBegin;
    private String signEnd;
    private String busInTime;
    private String busOutTime;
    private String industryCode;
    private String appVerCode;
    private String gridId;
}
