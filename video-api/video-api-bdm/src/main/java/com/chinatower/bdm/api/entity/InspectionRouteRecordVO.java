package com.chinatower.bdm.api.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class InspectionRouteRecordVO implements Serializable {

    /**
     * 巡检路线数量
     */
    private Integer inspectionRouteCount;

    /**
     * 巡检地块数量
     */
    private Integer inspectPlotNum;

    /**
     * 巡检次数
     */
    private Integer inspectionTimes;

    /**
     * 巡检时长 单位：小时
     */
    private Long inspectionDuration;
}