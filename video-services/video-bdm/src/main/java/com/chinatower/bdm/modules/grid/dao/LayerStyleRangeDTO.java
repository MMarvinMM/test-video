package com.chinatower.bdm.modules.grid.dao;

import lombok.Builder;
import lombok.Data;

/**
 * @desription: 样式范围
 * @data 创建时间：2024/12/3 14:18
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@Builder
public class LayerStyleRangeDTO {
    /**
     * 值
     */
    private String value;
    /**
     * 比较字符
     */
    private String symbol;
}
