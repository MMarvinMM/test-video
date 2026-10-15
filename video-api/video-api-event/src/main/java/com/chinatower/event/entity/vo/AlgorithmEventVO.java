package com.chinatower.event.entity.vo;


import lombok.Data;

/**
 * @Auther: dengyl
 * @Date: 2021年12月2日13:04:53
 * @Description:
 * 告警算法信息
 */
@Data
public class AlgorithmEventVO {
    private static final long serialVersionUID = 1L;
    public static final String SECOND = "s";
    public static final String MINUTES = "M";
    public static final String HOUR = "h";
    public static final String DAY = "d";
    public static final String FOR_THRESH = "1"; //置信度压制
    public static final String FOR_ALARM_INTERVAL = "2"; //重复告警压制
    public static final String FOR_ALGORITHM_TIME = "3"; //告警时间压制
    public static final String FOR_JUDGE = "4"; //研判压制
    public static final String FOR_AREA = "5"; //区域屏蔽压制
    public static final String FOR_ALGORITHM = "6"; //算法阈值压制
    public static final String RAINY_ALGORITHM = "7"; //雨天模式压制
    public static final String UAV_FLY_TIME_INTERVAL = "8"; //起飞后告警时间压制
    public static final int TIME = 180;
    public static final int MTIME = 60;
    public static final int HTIME = 3600;
    public static final int DTIME = 86400;
    public static final int DOUBLE = 2;
    public static final String ZERO = "0";
    public static final String FOUR = "4";
    public static final String TABLENAME = "vm_alarm_operate_t";

    /**
     * 客户编码
     */
    private String customerCode;
    /**
     * 任务id
     */
    private String taskId;
    /**
     * 置信度
     */
    private Integer thresh;
    /**
     * 告警时间间隔
     */
    private Integer alarmInterval;
    /**
     * 告警时间间隔单位
     */
    private String alarmIntervalType;
    /**
     * 运行时间
     */
    private String algorithmTime;
    /**
     * 告警时间
     */
    private String alarmTime;

    /** 是否开启运营研判 0:否 1:是 */
    private String isOperation;


}
