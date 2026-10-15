package com.chinatower.bdm.api.entity;

import lombok.Data;

/**
 * @desription: 图层图斑数据
 * @data 创建时间：2024/4/24 10:39
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerAreaCountVO {

    /**
     * 图层ID
     */
    private String layerId;
    /**
     * 图层名称
     */
    private String layerName;
    /**
     * 图层面积
     */
    private Double layerArea;
    /**
     * 图层图斑数量
     */
    private Integer polygonCount;

}
