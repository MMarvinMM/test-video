package com.chinatower.airline.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 云台控制基础参数类
 *
 * @author ljy
 * @date 2025/12/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("云台控制基础参数类")
public class BaseAirDeviceWithNodeInfoReq extends BaseAirDeviceReq{
    
    @ApiModelProperty("接入节点")
    @NotNull(message = "接入节点不能为空")
    private String accessNode;
    
    @ApiModelProperty(value = "接收节点", hidden = true)
    private String acceptNode;
    
}
