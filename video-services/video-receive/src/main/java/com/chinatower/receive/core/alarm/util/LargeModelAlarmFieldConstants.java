package com.chinatower.receive.core.alarm.util;

/**
 * 大模型告警 extendField / extraField 拆分相关常量
 */
public final class LargeModelAlarmFieldConstants {

    private LargeModelAlarmFieldConstants() {
    }

    /** CVLM 大模型告警信息 attrType */
    public static final String ATTR_TYPE_CVLM_ALARM_INFO = "CVLM-ALARM-INFO";

    /** 标绘区域 monitorType */
    public static final int MONITOR_TYPE_DRAW_AREA = 0;

    /** JSON 字段名 */
    public static final String JSON_FIELD_ALARM_AREA_ID = "alarmAreaId";
    public static final String JSON_FIELD_ALARM_TYPE = "alarmType";
    public static final String JSON_FIELD_ALARM_AREA_LIST = "alarmAreaList";
    public static final String JSON_FIELD_ALARM_AREA = "alarmArea";
    public static final String JSON_FIELD_ATTR_LIST = "attrList";
    public static final String JSON_FIELD_ATTR_TYPE = "attrType";
    public static final String JSON_FIELD_ATTR_VALUE = "attrValue";
    public static final String JSON_FIELD_EXTEND_FIELD = "extendField";
    public static final String JSON_FIELD_EXTRA_FIELD = "extraField";

    /** CVLM 外层节点 alarmAreaId */
    public static final String CVLM_ALARM_AREA_ID = "CVLM";
}
