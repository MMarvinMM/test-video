package com.chinatower.device.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 无人机飞行半径VO
 */
@Data
public class FlightRadiusVo  implements Serializable {

    /**
     * 无人机编码
     */
    @ApiModelProperty(value = "无人机编码")
    private String uavCode;

    /**
     * 设备编码
     */
    @ApiModelProperty(value = "设备型号编码")
    private String modelCode;

    /**
     * 飞行半径
     */
    @ApiModelProperty(value = "飞行半径")
    private Double flightRadius;
}
