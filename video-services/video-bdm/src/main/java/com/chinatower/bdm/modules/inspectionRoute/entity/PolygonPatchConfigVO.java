package com.chinatower.bdm.modules.inspectionRoute.entity;


import lombok.Data;

import java.io.Serializable;

/**
 * 电子围栏信息实体类
 */
@Data
public class PolygonPatchConfigVO implements Serializable {


    /**
     * 电子围栏ID
     */
    private String polygonId;

    /**
     * 电子围栏前端排序ID
     */
    private String monitorAreaId;

    /**
     * 电子围栏排序
     */
    private Integer polygonNo;

    /**
     * 区域点集合json
     */
    private String polygonPoints;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 预置位ID
     */
    private String presetId;

    /**
     * 预置位名称
     */
    private String presetName;

    /**
     * 电子围栏名称
     */
    private String polygonName;

    /**
     * 图层ID
     */
    private String layerId;

    /**
     * 图层名称
     */
    private String layerName;

    /**
     * 图斑ID
     */
    private String featureId;

    /**
     * 图斑编号
     */
    private String tbbh;

    private String creator;
    private String modifier;
    private String tenantId;
    private String industryCode;
    private String appVerCode;

    /**
     * 巡检路线关联表ID
     */
    private Long inspectionRelId;

}
