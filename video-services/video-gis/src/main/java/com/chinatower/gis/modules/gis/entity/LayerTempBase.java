package com.chinatower.gis.modules.gis.entity;

import lombok.Data;

/**
 * @desription: 模板公告数据
 * @data 创建时间：2023/8/12 15:35
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerTempBase {

    private String geom;
    /**
     * bsm
     */
    private String bsm;
    /**
     * layertype
     */
    private String layertype;

    /**
     * 属性json字符串
     */
    private String properties;
}
