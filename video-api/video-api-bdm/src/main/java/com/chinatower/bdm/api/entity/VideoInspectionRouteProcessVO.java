package com.chinatower.bdm.api.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class VideoInspectionRouteProcessVO implements Serializable {

    /**
     * 巡检路线编码
     * @required true
     */
    private Long inspectionRouteId;

    /**
     * 巡检路线下具体某一点位编码
     * @required true
     */
    private Long inspectionRouteSubId;

    /**
     * 巡检路线状态 0-空闲 1-巡查 2-暂停
     */
    private Integer routeStatus;

    /**
     * 排序序号
     * @required true
     */
    private Integer sortSeq;

    /**
     * 提示消息
     */
    private String alertMessage;
}
