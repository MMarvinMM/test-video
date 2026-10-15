package com.chinatower.airline.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 云台拖拽入参
 *
 * @author ljy
 * @date 2026/1/5
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CameraScreenDragReq extends BaseAirDeviceWithNodeInfoReq{

    @ApiModelProperty("云台pitch速度")
    @NotNull(message = "云台pitch速度不能为空")
    private float pitchSpeed;
    @ApiModelProperty("云台yaw速度")
    @NotNull(message = "云台yaw速度不能为空")
    private float yawSpeed;
    @ApiModelProperty(value = "机头和云台的相对关系是否锁定",notes = "false-仅云台转，机身不转,true-锁定机头，云台和机身一起转")
    private Boolean locked;

}
