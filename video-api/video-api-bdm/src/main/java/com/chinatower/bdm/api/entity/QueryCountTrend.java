package com.chinatower.bdm.api.entity;

import lombok.Data;

/**
 * 按日期统计用户巡查次数趋势
 *
 * @author dencycheng
 */
@Data
public class QueryCountTrend {
    /**
     * 开始日期
     * @required true
     */
    private String beginTime;
    /**
     * 结束日期
     * @required true
     */
    private String endTime;
    /**
     * 用户id
     * @required true
     */
    private String userId;

}
