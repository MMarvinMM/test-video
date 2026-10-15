package com.chinatower.gis.api.entity.layer;

import lombok.Data;

/**
 * @desription: 深林图层图例
 * @data 创建时间：2023/12/5 9:34
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class ForestLayerLegendRes {

    /**
     * 字段代码（根据条件传参返回的对应字段）
     */
    private String fieldCode;
    /**
     * 字段值
     */
    private String fieldValue;
    /**
     *  名称
     */
    private String fieldName;
    /**
     * 统计 （亩）
     */
    private Double totalMj;
    /**
     * 占比
     */
    private String ratio;

}
