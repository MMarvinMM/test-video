package com.chinatower.common.core.dto.bdm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @desription: 深林资源统计
 * @data 创建时间：2024/5/31 11:01
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerCountAndAreaDTO {
    /**
     * 图层总面积
     */
    @ApiModelProperty(value = "图层总面积")
    private Double layerArea;
    /**
     * 图层图斑总数
     */
    @ApiModelProperty(value = "图层图斑总数")
    private Integer polygonCount;
}
