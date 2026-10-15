package com.chinatower.bdm.modules.grid.dao;

import lombok.Builder;
import lombok.Data;

/**
 * @desription: 样式填充的属性
 * @data 创建时间：2024/10/17 15:18
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@Builder
public class LayerStyleFiledParamsDTO {
    /**
     * 图例值
     */
    private String ruleKey;
    /**
     * 图例字典值
     */
    private String ruleValue;
    /**
     * 图例字段
     */
    private String ruleCode;
    /**
     * 填充颜色
     */
    private String fillColor;
    /**
     * 标签颜色
     */
    private String labelColor;
    /**
     * 填充透明度
     */
    private String fillOpacity;
    /**
     * 线条颜色
     */
    private String strokeColor;
    /**
     * 线条透明度
     */
    private String strokeOpacity;
    /**
     * 线条宽度
     */
    private String strokeWidth;
    /**
     * 是否范围
     */
    private Boolean rangeFlag;
    /**
     * 范围
     */
    private LayerStyleRangeDTO min;
    /**
     * 范围
     */
    private LayerStyleRangeDTO max;
}
