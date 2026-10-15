package com.chinatower.airline.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 切换镜头参数
 *
 * @author wlf
 * @date 2026/7/8
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("切换镜头参数")
public class CameraZoomReq extends BaseAirDeviceWithNodeInfoReq {

    @ApiModelProperty(value = "镜头类型", notes = "1：广角，2：变焦，3：红外")
    @NotNull(message = "镜头类型不能为空")
    private Integer source;

    @ApiModelProperty("获取云台控制权后，飞控平台返回的令牌token")
    private String xToken;
}
