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
public class LayerCountRes {
    /**
     * 1-矢量 2-非矢量 3-DEM 4-DSM 5-倾斜摄影 6-激光点云
     */
    private String category;
    /**
     * 图层总数
     */
    private int layerCount;
}
