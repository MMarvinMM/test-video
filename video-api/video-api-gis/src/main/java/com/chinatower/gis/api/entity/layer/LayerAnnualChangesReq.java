package com.chinatower.gis.api.entity.layer;

import lombok.Data;

import java.util.List;

/**
 * @desription: 年度变化
 * @data 创建时间：2024/6/4 16:42
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerAnnualChangesReq {

    /**
     * 类型 1-耕地 2-基本农田
     */
    private Integer type;

    /**
     * 多个年份的数据
     */
    private List<LayerYearTableName> tableNames;
}
