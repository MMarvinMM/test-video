package com.chinatower.bdm.api.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class VideoInspectionRouteRecordVO implements Serializable {

    /**
     * 巡检路线数量
     */
    private Integer inspectionRouteCount;

    /**
     * 巡检次数
     */
    private Integer inspectionTimes;

    /**
     * 巡检时长 单位：小时
     */
    private Long inspectionDuration;
}