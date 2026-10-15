package com.chinatower.gis.api.entity.layer;

import lombok.Data;

/**
 * @desription:
 * @data 创建时间：2024/6/4 15:44
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerYearTableName {

    /**
     * 年分
     */
    private Integer year;
    /**
     * pg 表名称集合
     */
    private String tableName;
}
