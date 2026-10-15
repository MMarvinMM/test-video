package com.chinatower.bdm.api.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class InspectionRoutePage4SdkVO implements Serializable {

    /**
     * 巡检路线编码
     * @required true
     */
    private Long inspectionRouteId;

    /**
     * 巡检路线名称
     * @required true
     */
    private String inspectionRouteName;

    /**
     * 巡检路线状态 0-空闲 1-巡查 2-暂停
     */
    private Integer routeStatus;

    /**
     * 关联图层名称
     * @required true
     */
    private String layerNames;

    /**
     * 自定义图层名称
     */
    private String customNames;

    /**
     * 地块数量
     * @required true
     */
    private Integer plotNum;

    /**
     * 地块总面积
     * @required true
     */
    private Integer plotTotalArea;

    /**
     * 总时长
     */
    private String totalDurationTime;
}
