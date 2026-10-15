package com.chinatower.gis.api.entity;

import lombok.Data;

/**
 * @desription: 添加图层返回实体
 * @data 创建时间：2023/8/14 15:57
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerPGRes {
    /**
     * 图层总面积
     */
    private Double layerArea;
    /**
     * 图斑总数
     */
    private Integer polygonCount;

    /**
     * 图层ID
     */
    private String layerId;
    /**
     * 图层表名
     */
    private String pgTableName;
}
