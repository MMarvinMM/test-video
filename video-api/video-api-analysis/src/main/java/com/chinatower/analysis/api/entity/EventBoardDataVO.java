package com.chinatower.analysis.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 告警看板VO
 */
@Data
public class EventBoardDataVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 年
     * @required true
     */
    private String year;
    /**
     * 月
     * @required true
     */
    private String month;
    /**
     * 日
     * @required true
     */
    private String day;
    /**
     * 当日告警总数
     * @required true
     */
    private int todayAll;

    /**
     * 真警数量
     * @required true
     */
    private int realNum;
}
