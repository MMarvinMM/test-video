package com.chinatower.platform.project.noticeRule.entity;

import lombok.Data;

import java.util.List;

@Data
public class NoticeAlarmSourceVO {
    //来源标识
    private String alarmSource;

    //来源标识名称
    private String alarmSourceName;

    //告警类型列表
    private List<NoticeAlarmTypeVO> alarmTypeVOList;
}
