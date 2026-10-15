package com.chinatower.airline.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 镜头转向参数
 *
 * @author wlf
 * @date 2026/6/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("镜头转向参数")
public class ControlGimbalReq extends BaseAirDeviceWithNodeInfoReq {

    @ApiModelProperty(value = "俯仰角", notes = "角度，上下（增量转动，给多少转多少）")
    @NotNull(message = "俯仰角不能为空")
    private Integer pitchAngle;

    @ApiModelProperty(value = "朝向", notes = "角度，左右，仅不锁机头时才生效")
    @NotNull(message = "朝向不能为空")
    private Integer yamAngle;

    @ApiModelProperty(value = "机位标识", notes = "目前除了G-503外，都只有一个无人机，写0。1、2、3分别对应G-503三个无人机机位")
    @NotNull(message = "机位标识不能为空")
    private String which;

    @ApiModelProperty("获取云台控制权后，飞控平台返回的令牌token")
    private String xToken;
}
