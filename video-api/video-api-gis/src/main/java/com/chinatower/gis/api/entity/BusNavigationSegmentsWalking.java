package com.chinatower.gis.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @desription: 公交步行路段
 * @data 创建时间：2023/8/9 11:41
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class BusNavigationSegmentsWalking {
    /**
     * 起点经纬度
     * @required false
     */
    @ApiModelProperty(value = "起点经纬度")
    private String origin;

    /**
     * 终点经纬度
     * @required false
     */
    @ApiModelProperty(value = "终点经纬度")
    private String destination;

    /**
     * 步行预计时间
     * @required false
     */
    @ApiModelProperty(value = "步行预计时间")
    private String distance;

    /**
     * 终点经纬度
     * @required false
     */
    @ApiModelProperty(value = "终点经纬度")
    private List<BusNavigationSegmentsWalkingSteps> steps;

}
