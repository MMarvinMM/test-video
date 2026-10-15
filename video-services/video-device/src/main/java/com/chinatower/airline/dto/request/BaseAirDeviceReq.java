package com.chinatower.airline.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 设备编号请求参数
 *
 * @author ljy
 * @date 2025/12/30
 */
@ApiModel("设备编号请求参数")
@Data
public class BaseAirDeviceReq {
    @ApiModelProperty("设备编号(取机巢编号uavNestCode)")
    @NotNull(message = "设备编号不能为空")
    private String deviceCode;
}
