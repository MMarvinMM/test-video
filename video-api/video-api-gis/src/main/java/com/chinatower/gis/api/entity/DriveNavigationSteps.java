package com.chinatower.gis.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @desription:
 * @data 创建时间：2023/8/9 9:06
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class DriveNavigationSteps {

    /**
     * 行驶指示
     * @required false
     */
    @ApiModelProperty(value = "行驶指示")
    private String instruction;
    /**
     * 时长 单位：秒
     */
    private String duration;
    /**
     * 进入道路方向
     * @required false
     */
    @ApiModelProperty(value = "进入道路方向")
    private String orientation;

    /**
     * 分段道路名称
     * @required false
     */
    @ApiModelProperty(value = "分段道路名称")
    private String roadName;

    /**
     * 分段距离信息
     * @required false
     */
    @ApiModelProperty(value = "分段距离信息")
    private String stepDistance;
    /**
     * 分段距离信息
     * @required false
     */
    private String distance;
    private Object navi;
    private Object tmcs;
    private Object cost;
    private Object cities;
    /**
     * 线路
     */
    private String polyline;
}
