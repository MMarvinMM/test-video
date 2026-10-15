package com.chinatower.bdm.modules.inspectionRoute.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class InspectionRouteFenceVO implements Serializable {


    /**
     * 巡检路线ID
     */
    private Long inspectionRouteId;

    /**
     * 巡检路线名称
     */
    private String inspectionRouteName;

    /**
     * 是否暂停巡航 0-否 1-是
     */
    private Integer pauseCruiseFlag;

    /**
     * 电子围栏集合
     */
    private List<InspectionRelVO> inspectionRelList;
}
