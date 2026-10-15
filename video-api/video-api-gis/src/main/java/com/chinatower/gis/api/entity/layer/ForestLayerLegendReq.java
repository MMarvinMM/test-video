package com.chinatower.gis.api.entity.layer;

import lombok.Data;

/**
 * @desription: 图例查询
 * @data 创建时间：2023/12/5 9:52
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class ForestLayerLegendReq {

    /**
     * 图层ID
     */
    private String layerId;
    /**
     * 深林专题查询类型 详细查询 SpecialSubjectEnums
     * {@link com.chinatower.platform.enums.SpecialSubjectEnums}
     */
    @Deprecated
    private Integer type;
    /**
     * 字段code
     */
    private String code;
    /**
     * 是否需要占比（1-需要）
     */
    private int needRatio = 0;

}
