package com.chinatower.gis.api.entity.layer;

import lombok.Data;

/**
 * @desription: 年度变化返回
 * @data 创建时间：2024/6/4 16:50
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerAnnualChangesRes {


    /**
     * 年分
     */
    private Integer year;
    /**
     * 面积
     */
    private String area;
}
