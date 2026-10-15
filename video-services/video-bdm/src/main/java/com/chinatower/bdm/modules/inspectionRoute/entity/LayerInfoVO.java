package com.chinatower.bdm.modules.inspectionRoute.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class LayerInfoVO implements Serializable {

    /**
     * 数据ID
     */
    private String id;

    /**
     * 自定义图层名称
     */
    private String customName;

    /**
     * 图层类别
     */
    private String layerTypeName;

    /**
     * 简称
     * */
    private String abbreviation;

    /**
     *  图层总面积
     * */
    private String layerArea;

    /**
     * 图层类别ID
     */
    private String layerTypeId;

    /**
     * geoserver地址
     */
    private String geoserverUrl;

    /**
     * geoserver图层名称
     */
    private String layerName;


    /**
     * geoserver图层命名空间
     */
    private String layerNamespace;

    /**
     * 数据属性
     */
    private String gameKey;

    /**
     * 最大X轴
     */
    private Double maxx;

    /**
     * 最大Y轴
     */
    private Double maxy;

    /**
     * 最小X轴
     */
    private Double minx;

    /**
     * 最小Y轴
     */
    private Double miny;
}
