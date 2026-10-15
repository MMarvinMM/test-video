package com.chinatower.common.core.dto.bdm;

import lombok.Data;

/**
 * @desription: 更新图层面积
 * @data 创建时间：2024/4/2 14:43
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class UpdateAreaDTO {
    /**
     * 图层类别 0-sys_layer 1-vm_satellite_layer_config
     */
    private int type;
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
     * 简称
     */
    private String abbreviation;
}
