package com.chinatower.platform.modules.alarm.util.subscenes;

/**
 * CVLM extra_field 字段常量（刷数侧精简副本）。
 * <p>与 video-receive LargeModelAlarmFieldConstants 同源，后续可抽公共模块。</p>
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
