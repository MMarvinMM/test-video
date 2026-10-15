package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * bm_attendance_line
 * @author
 */
@Data
public class BmAttendanceSetVO extends BaseDataScopeEntity implements Serializable {
    /**
     * 签入频率,ONE|TWO
     */
    private String signInFrequency;

    /**
     * 考勤时间段,AM:上午 、AM:下午、AL:全天
     */
    private String signTimeSegment;

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
     * 签到结束时间
     */
    private String signOver;

    /**
     * 签退开始时间
     */
    private String signEnd;


    /**
     * 签退结束时间
     */
    private String signClose;

    /**
     * 工作日
     */
    private String onWeekdays;

    /**
     * 1 跟随网格 2 指定地点
     */
    private String signType;

    /**
     * 打卡地址
     */
    private String signAddress;

    /**
     * 打卡经纬度
     */
    private String signLnglat;

    /**
     * 打卡范围
     */
    private String signRange;

    /**
     * 工作日名称
     */
    private String onWeekdaysName;


    /**
     * 网格打卡范围
     */
    private List<String> signGridRange;

    private transient List<Map<String, Object>> signGridNameAndRange;



    private static final long serialVersionUID = 1L;

    private String gridId;
}
