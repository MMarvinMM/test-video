package com.chinatower.common.core.dto.bdm;

import lombok.Data;

/**
 * @desription: 深林资源类型统计
 * @data 创建时间：2024/5/31 13:39
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerSlzyStatisticsDTO {
    /**
     * 统计的字段
     */
    private String code;
    /**
     * 字段值
     */
    private String value;
    /**
     * 面积
     */
    private Double area;
    /**
     * 数量
     */
    private Integer count;
    /**
     * 占比
     */
    private String ratio;
}
