package com.chinatower.order.vo;

import com.chinatower.order.dto.UavDeviceTrackDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 无人机遥测信息
 */
@Data
public class UavDeviceSyncVO implements Serializable {

    @ApiModelProperty(value = "时间戳")
    private Long timestamp;

    @ApiModelProperty(value = "设备编号")
    @NotBlank(message = "设备编码不能为空")
    private String deviceCode;

    @ApiModelProperty(value = "经纬度数据")
    private UavDeviceTrackDTO data;
}
