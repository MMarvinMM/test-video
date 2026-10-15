package com.chinatower.bdm.modules.inspectionRoute.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class InspectionFeaturePlotVO implements Serializable {

    /**
     * 图斑ID
     */
    private String featureId;

    /**
     * 图斑大小
     */
    private String plotArea;

    /**
     * 巡检路线ID
     */
    private Long inspectionRouteId;
}
