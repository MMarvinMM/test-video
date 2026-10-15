package com.chinatower.airline.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 切换直播镜头参数
 *
 * @author ljy
 * @date 2025/12/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel ("变焦参数")
public class ChargeRatioReq extends BaseAirDeviceWithNodeInfoReq {

    @ApiModelProperty( value = "变焦倍数")
    @NotNull(message = "变焦倍数不能为空")
    private Double ratio;

    @ApiModelProperty(value = "是否为红外相机",notes = "0-否；1-是 0时为可见光变焦，1时是红外变焦（镜头从运管获取，或直接调用，后台会兜底，无功能会返回失败）")
    @NotNull(message = "是否为红外相机不能为空")
    private boolean thermal;
    @ApiModelProperty("获取云台控制权后，飞控平台返回的令牌token")
    private String xToken;
}
