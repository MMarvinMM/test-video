package com.chinatower.gis.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @desription: 公交导航信息返回
 * @data 创建时间：2023/8/9 10:56
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class BusNavigationRes {


    /**
     * 起点经纬度
     * @required true
     */
    @ApiModelProperty(value = "起点经纬度")
    private String origin;

    /**
     * 终点经纬度
     * @required true
     */
    @ApiModelProperty(value = "终点经纬度")
    private String destination;

    /**
     * 公交方案列表
     * @required true
     */
    @ApiModelProperty(value = "公交方案列表")
    private List<BusNavigationTransits> transits;
}
