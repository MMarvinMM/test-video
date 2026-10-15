package com.chinatower.gis.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @desription:
 * @data 创建时间：2023/8/9 13:42
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class BusNavigationSegmentsBusBuslines {

    /**
     * 公交路线名称
     * @required false
     */
    @ApiModelProperty(value = "公交路线名称")
    private String name;

    /**
     * 公交路线id
     * @required false
     */
    @ApiModelProperty(value = "公交路线id")
    private String id;

    /**
     * 公交类型
     * @required false
     */
    @ApiModelProperty(value = "公交类型")
    private String type;

    /**
     * 公交行驶距离
     * @required false
     */
    @ApiModelProperty(value = "公交行驶距离")
    private String distance;

    /**
     * 公交预计行驶时间
     * @required false
     */
    @ApiModelProperty(value = "公交预计行驶时间")
    private String duration;

    /**
     * 此路段坐标集
     * @required false
     */
    @ApiModelProperty(value = "此路段坐标集")
    private String polyline;

    /**
     * 首班车时间
     * @required false
     */
    @ApiModelProperty(value = "首班车时间")
    private String startTime ;

    /**
     * 末班车时间
     * @required false
     */
    @ApiModelProperty(value = "末班车时间")
    private String endTime ;

    /**
     * 此段途经公交站数
     * @required false
     */
    @ApiModelProperty(value = "此段途经公交站数")
    private String viaNum ;

    /**
     * 此段起乘站信息
     * @required false
     */
    @ApiModelProperty(value = "此段起乘站信息")
    private BusNavigationSegmentsBusBuslinesStop departureStop;

    /**
     * 此段下车站
     * @required false
     */
    @ApiModelProperty(value = "此段下车站")
    private BusNavigationSegmentsBusBuslinesStop arrivalStop;

    /**
     * 此段途经公交站点列表
     * @required false
     */
    @ApiModelProperty(value = "此段途经公交站点列表")
    private List<BusNavigationSegmentsBusBuslinesStop> viaStops;
}
