package com.chinatower.event.constant;

/**
 * AI 告警 extraField 解析相关常量
 */
public final class AiExtraFieldConstants {

    private AiExtraFieldConstants() {
    }

    /** CVLM 大模型告警信息 attrType */
    public static final String ATTR_TYPE_CVLM_ALARM_INFO = "CVLM-ALARM-INFO";

    /** 车辆/人员密度实际触发数量 attrType */
    public static final String ATTR_TYPE_OBJECT_COUNT = "objectCount";

    /** extraField JSON 字段名 */
    public static final String JSON_FIELD_ATTR_LIST = "attrList";
    public static final String JSON_FIELD_ATTR_TYPE = "attrType";
    public static final String JSON_FIELD_ATTR_VALUE = "attrValue";
    public static final String JSON_FIELD_EXTEND_FIELD = "extendField";
}
