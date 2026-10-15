package com.chinatower.event.constant;

/**
 * @ClassName AlarmConstants
 * @Deacription 告警常量类
 * @Author chenmf
 * @Date 2021/12/2 16:14
 * @Version 1.0
 **/
public class AlarmConstants {

    private AlarmConstants(){}

    /**
     1-算法AI告警；
     2-雷达告警；
     3-无人机告警；
     4-摄像机算法告警；
     5-车辆卡口摄像机告警；
     6-人脸卡口摄像机告警；
     7-行业告警；
     8-物联网告警；
     9-卫星数据
     12-无人机告警
     */
    public static final String ALARM_SOURCE_1 = "1";
    public static final String ALARM_SOURCE_2 = "2";
    public static final String ALARM_SOURCE_3 = "3";
    public static final String ALARM_SOURCE_4 = "4";
    public static final String ALARM_SOURCE_5 = "5";
    public static final String ALARM_SOURCE_6 = "6";
    public static final String ALARM_SOURCE_7 = "7";
    public static final String ALARM_SOURCE_8 = "8";
    public static final String ALARM_SOURCE_9 = "9";
    public static final String ALARM_SOURCE_10 = "10";
    public static final String ALARM_SOURCE_11 = "11";
    public static final String ALARM_SOURCE_12 = "12";


    //0-主告警；1-从告警
    public static final int ALARM_FLAG_0 = 0;
    public static final int ALARM_FLAG_1 = 1;

    public static final String ALARM_REL_CACHE_KEY = "ALARM_REL_";

    //物联告警回调地址字典类型值
    public static final String IOT_SNAP_SHOT_BACK_URL = "iot_snap_shot_back_url";
    //卫星告警回调地址字典类型值
    public static final String SATELLITE_SNAP_SHOT_BACK_URL = "satellite_snap_shot_back_url";
}
