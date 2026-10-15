package com.chinatower.event.vo;

import lombok.Data;

/**
 * 告警等级VO
 */
@Data
public class AlarmLevelVO {

    /**
     * 告警类型编码
     */
    private String typeValue;

    /**
     * 告警等级值
     */
    private String alarmLevel;

    /**
     * 告警等级名称
     */
    private String alarmLevelName;

}
