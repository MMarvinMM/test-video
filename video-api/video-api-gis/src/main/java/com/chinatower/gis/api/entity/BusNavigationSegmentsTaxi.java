package com.chinatower.gis.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @desription:
 * @data 创建时间：2023/8/9 13:49
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class BusNavigationSegmentsTaxi {

    /**
     * 打车预计花费金额
     * @required false
     */
    @ApiModelProperty(value = "打车预计花费金额")
    private String price;

    /**
     * 打车预计花费时间
     * @required false
     */
    @ApiModelProperty(value = "打车预计花费时间")
    private String drivetime;

    /**
     * 打车距离
     * @required false
     */
    @ApiModelProperty(value = "打车距离")
    private String distance;

    /**
     * 线路点集合，通过show_fields控制返回与否
     * @required false
     */
    @ApiModelProperty(value = "线路点集合，通过show_fields控制返回与否")
    private String polyline;

    /**
     * 打车起点经纬度
     * @required false
     */
    @ApiModelProperty(value = "打车起点经纬度")
    private String startpoint;

    /**
     * 打车起点名称
     * @required false
     */
    @ApiModelProperty(value = "打车起点名称")
    private String startname;

    /**
     * 打车终点经纬度
     * @required false
     */
    @ApiModelProperty(value = "打车终点经纬度")
    private String endpoint;

    /**
     * 打车终点名称
     * @required false
     */
    @ApiModelProperty(value = "打车终点名称")
    private String endname;

}
