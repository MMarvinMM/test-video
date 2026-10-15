package com.chinatower.platform.modules.keeper.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 网格员考勤信息实体类
 */
@Data
@ToString
public class BmAttendanceLineTaskVO implements Serializable {
    private static final long serialVersionUID = 1L;

    //流水ID，主键
    private String attendanceId;
    //租户编码
    private String tenantId;
    //网格员ID
    private String keeperId;
    //网格员名称
    private String keeperName;
    //考勤时间段
    private String signTimeSegment;
    //签到状态:1正常,2迟到,3未签到,7缺勤
    private String signInStatus;
    //签到时间
    private String signInAddTime;
    //签到所在网格
    private String signInGrid;
    //签到是否网格内
    private String signInOutside;
    //签到经纬度
    private String signInCoordinate;
    //签到地址
    private String signInAddress;
    //签退状态:4正常,5早退,6未签退
    private String signOutStatus;
    //签退时间
    private String signOutAddTime;
    //签退所在网格
    private String signOutGrid;
    //签退是否网格内
    private String signOutOutside;
    //签退经纬度
    private String signOutCoordinate;
    //签退地址
    private String signOutAddress;
    //考勤日期
    private String attendanceDate;
    //创建人
    private String creator;
    //修改人
    private String modifier;
    //创建时间
    private String createTime;
    //删除状态, 0-正常；1-删除
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
    //行业编码
    private String industryCode;
    //行业版本编码
    private String appVerCode;
    private String weekDay;
    //网格ID
    private String gridId;
}
