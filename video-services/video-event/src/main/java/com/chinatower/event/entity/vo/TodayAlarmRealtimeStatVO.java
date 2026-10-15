package com.chinatower.event.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 首页告警卡片实时统计（合并 todayAll / penddingJudged / realNum）
 */
@Data
public class TodayAlarmRealtimeStatVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 今日告警总数（alarm_time 为今日） */
    private Integer todayAll;

    /** 今日待研判数量（今日 alarm_time 且 order_status=1，非验收模式） */
    private Integer penddingJudged;

    /** 今日真警数量（今日 alarm_time 且 judge_result=1） */
    private Integer realNum;
}
