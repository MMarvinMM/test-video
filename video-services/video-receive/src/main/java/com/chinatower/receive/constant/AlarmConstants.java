package com.chinatower.receive.constant;

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


    //0-主告警；1-从告警
    public static final int ALARM_FLAG_0 = 0;
    public static final int ALARM_FLAG_1 = 1;

    public static final String ALARM_REL_CACHE_KEY = "ALARM_REL_";

    //物联告警回调地址字典类型值
    public static final String IOT_SNAP_SHOT_BACK_URL = "iot_snap_shot_back_url";
    //卫星告警回调地址字典类型值
    public static final String SATELLITE_SNAP_SHOT_BACK_URL = "satellite_snap_shot_back_url";

    public static final String LARGE_MODEL_SCENE_ALARM_TOPIC = "largeModelSceneAlarm";

    /** 研判状态：未研判 */
    public static final int AI_JUDGE_STATUS_PENDING = 0;
    /** 研判状态：研判完成 */
    public static final int AI_JUDGE_STATUS_DONE = 2;
    /** 研判状态：研判失败 */
    public static final int AI_JUDGE_STATUS_FAIL = 3;

    /**
     * 大模型标绘/屏蔽区域 modelConfig code 字典类型
     * dict_value 示例：dict-cvModeType、dict-emergencyCvModeType、dict-agricultureCvModeType
     */
    public static final String LARGE_MODEL_DRAW_AREA_CONFIG_CODE = "large_model_draw_area_config_code";
}
