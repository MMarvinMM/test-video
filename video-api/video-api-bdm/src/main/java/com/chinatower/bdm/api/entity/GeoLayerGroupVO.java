package com.chinatower.bdm.api.entity;

import lombok.Data;

/**
 * @desription: 图层组
 * @data 创建时间：2024/8/1 9:14
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GeoLayerGroupVO {

    /**
     * 图层名称
     */
    private String layerName;
    /**
     * 默认样式名称
     */
    private String defaultStyle;
}
