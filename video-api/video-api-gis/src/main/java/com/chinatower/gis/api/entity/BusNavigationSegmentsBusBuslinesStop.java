package com.chinatower.gis.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @desription:
 * @data 创建时间：2023/8/9 13:46
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class BusNavigationSegmentsBusBuslinesStop {

    /**
     * 站点名字
     * @required false
     */
    @ApiModelProperty(value = "站点名字")
    private String name;

    /**
     * 站点id
     * @required false
     */
    @ApiModelProperty(value = "站点id")
    private String id;

    /**
     * 站点经纬度
     * @required false
     */
    @ApiModelProperty(value = "站点经纬度")
    private String location;

    /**
     * 入口 name 入口名称  location 经纬度
     * @required false
     */
    @ApiModelProperty(value = "入口 name 入口名称  location 经纬度 ")
    private Object entrance;

    /**
     * 入口 name 入口名称  location 经纬度
     * @required false
     */
    @ApiModelProperty(value = "入口 name 入口名称  location 经纬度 ")
    private Object exit;
}
