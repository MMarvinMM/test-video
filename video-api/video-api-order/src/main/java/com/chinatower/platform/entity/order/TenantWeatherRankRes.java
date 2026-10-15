package com.chinatower.platform.entity.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @desription: 租户下天气质量排行返回
 * @data 创建时间：2023/8/7 16:05
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class TenantWeatherRankRes {

    /**
     * 区域编码
     */
    @ApiModelProperty(value = "行政区域编码")
    private String areaCode;

    /**
     * 区域名称
     */
    @ApiModelProperty(value = "区域名称")
    private String areaName;


    /**
     * 经度
     */
    @ApiModelProperty(value = "经度坐标")
    private String longitude;

    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度坐标")
    private String latitude;

    @ApiModelProperty(value = "空气质量")
    private float aqi;
}
