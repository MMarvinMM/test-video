package com.chinatower.gis.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @desription:
 * @data 创建时间：2023/8/9 11:43
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class BusNavigationSegmentsWalkingSteps {

    /**
     * 此段路的行走介绍
     * @required false
     */
    @ApiModelProperty(value = "此段路的行走介绍")
    private String instruction;

    /**
     * 路的名字
     * @required false
     */
    @ApiModelProperty(value = "路的名字")
    private String road;

    /**
     * 此段路的距离
     * @required false
     */
    @ApiModelProperty(value = "此段路的距离")
    private String distance;

    /**
     * 此段路预计消耗时间
     * @required false
     */
    @ApiModelProperty(value = "此段路预计消耗时间")
    private String duration;

    /**
     * 此段路的坐标
     * @required false
     */
    @ApiModelProperty(value = "此段路的坐标")
    private String polyline;

    /**
     * 步行主要动作
     * @required false
     */
    @ApiModelProperty(value = "步行主要动作")
    private String action;

    /**
     * 步行辅助动作
     * @required false
     */
    @ApiModelProperty(value = "步行辅助动作")
    private String assistantAction;

    /**
     * 扩展
     */
    private Object navi;
    private Object cost;
    private Object cities;
}
