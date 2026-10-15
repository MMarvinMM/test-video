package com.chinatower.message.hms.dto;

import lombok.Data;

/**
 * HMS code 与告警类型映射（对应 hms-code-alarm-type-map.json 单条结构）
 */
@Data
public class HmsCodeAlarmTypeItem {
    private String code;
    private String msg;
    private String inSky;
    private String alarmType;
}
