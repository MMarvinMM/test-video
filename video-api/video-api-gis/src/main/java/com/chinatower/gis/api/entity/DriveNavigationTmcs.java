package com.chinatower.gis.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DriveNavigationTmcs {

    /**
     * "此段路的长度，单位：米
     */
    @ApiModelProperty(value = "此段路的长度，单位：米 ")
    private String distance;

    /**
     * 此段路的交通情况，未知、畅通、缓行、拥堵、严重拥堵
     */
    @ApiModelProperty(value = "此段路的交通情况，未知、畅通、缓行、拥堵、严重拥堵 ")
    private String status;

    /**
     * 此段路的轨迹，规格：x1,y1;x2,y2"
     */
    @ApiModelProperty(value = "此段路的轨迹，规格：x1,y1;x2,y2")
    private String polyline;
}
