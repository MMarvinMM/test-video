package com.chinatower.gis.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @desription: 图层信息
 * @data 创建时间：2024/4/28 11:36
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerInfoAddDTO {
    /**
     * 最大X轴
     */
    @ApiModelProperty(value = "最大X轴")
    private Double maxx;
    /**
     * 最大Y轴
     */
    @ApiModelProperty(value = "最大Y轴")
    private Double maxy;
    /**
     * 最小X轴
     */
    @ApiModelProperty(value = "最小X轴")
    private Double minx;
    /**
     * 最小Y轴
     */
    @ApiModelProperty(value = "最小Y轴")
    private Double miny;
    /**
     * 图层总面积
     */
    @ApiModelProperty(value = "图层总面积")
    private Double layerArea;
    /**
     * 图层图斑总数
     */
    @ApiModelProperty(value = "图层图斑总数")
    private Integer polygonCount;
    /**
     * 图层ID
     * @required true
     */
    private String layerId;
    /**
     * 图层类型名称
     * @required true
     */
    private String layerTypeName;
    /**
     * 图层名称简称
     * @required true
     */
    private String layerTypeSimple;
    /**
     * 图层表如果存在则不创建直接新增入库
     * @required true
     */
    private String pgTableName;
    /**
     * 几何类型
     */
    @ApiModelProperty(value = "几何类型")
    private String geoType;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 租户ID
     */
    private String industryCode;
    /**
     * 创建人
     */
    private String createBy;
}
