package com.chinatower.platform.project.noticeRule.entity;

import lombok.Data;

@Data
public class NoticeAlarmTypeVO {
    //告警类型标识
    private String alarmTypeId;

    //告警类型名称
    private String typeName;

    //告警类型编码
    private String typeValue;

    //来源标识
    private String alarmSource;

    //来源标识名称
    private String alarmSourceName;
}
