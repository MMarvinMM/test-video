package com.chinatower.event.core.alarm.util.subscenes;

/**
 * CVLM extra_field 字段常量（event 侧精简副本）。
 * <p>与 video-receive LargeModelAlarmFieldConstants / video-task SubScenesCvlmConstants 同源。</p>
 */
final class SubScenesCvlmConstants {

    private SubScenesCvlmConstants() {
    }

    /** CVLM 大模型告警信息 attrType */
    static final String ATTR_TYPE_CVLM_ALARM_INFO = "CVLM-ALARM-INFO";

    static final String JSON_FIELD_ATTR_LIST = "attrList";
    static final String JSON_FIELD_ATTR_TYPE = "attrType";
    static final String JSON_FIELD_ATTR_VALUE = "attrValue";
}
