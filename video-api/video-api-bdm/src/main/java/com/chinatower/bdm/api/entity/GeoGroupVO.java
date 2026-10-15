package com.chinatower.bdm.api.entity;

import lombok.Data;

import java.util.List;

/**
 * @desription: 图层组
 * @data 创建时间：2024/8/1 9:16
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GeoGroupVO {

    /**
     * 图层组名称
     */
    private String layerName;
    /**
     * 图层组数据
     */
    private List<GeoLayerGroupVO> layers;
}
