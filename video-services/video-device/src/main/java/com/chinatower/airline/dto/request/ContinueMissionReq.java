package com.chinatower.airline.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 继续航线
 *
 * @author ljy
 * @date 2026/1/4
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("继续航线参数类")
public class ContinueMissionReq extends BaseAirDeviceWithNodeInfoReq{
    
    @ApiModelProperty(value = "是否支持断点续飞",notes = "是否支持断点续飞。0-否；1-是 默认1-是")
    @NotNull(message = "是否支持断点续飞不能为空")
    private boolean breakPoint;

}
