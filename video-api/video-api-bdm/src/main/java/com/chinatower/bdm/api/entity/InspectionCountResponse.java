package com.chinatower.bdm.api.entity;

import lombok.Data;

/**
 * @author dencycheng
 */
@Data
public class InspectionCountResponse {

    /**
     * 巡查日期 (yyyy-MM-dd)
     * @required true
     */
    private String date;
    /**
     * 巡查次数
     * @required true
     */
    private Integer todayAll;
}
