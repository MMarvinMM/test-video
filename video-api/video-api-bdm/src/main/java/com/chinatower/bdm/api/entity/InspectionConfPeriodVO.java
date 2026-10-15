package com.chinatower.bdm.api.entity;

import lombok.Data;

/**
 * 巡查计划中巡查时段对象
 * @author zhaozj
 */
@Data
public class InspectionConfPeriodVO {
    //巡查时段开始时间
    private String inspectionStartTime;
    //巡查时段结束时间
    private String inspectionEndTime;
}
