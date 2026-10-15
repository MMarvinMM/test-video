package com.chinatower.platform.entity.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class UavDeviceTrackParam implements Serializable {

    /**
     * 查询开始时间
     * @required true
     */
    @ApiModelProperty(value = "查询开始时间")
    @NotBlank(message = "查询开始时间不能为空")
    private String beginTime;

    /**
     * 查询结束时间
     * @required false
     */
    @ApiModelProperty(value = "查询结束时间")
    private String endTime;

    /**
     * 设备编码
     * @required true
     */
    @ApiModelProperty(value = "设备编码")
    @NotBlank(message = "设备编码不能为空")
    private String deviceCode;

    /**
     * 是否由84坐标转成火星坐标
     * @required false
     */
    @ApiModelProperty(value = "是否由84坐标转成火星坐标")
    private boolean gps84ToGcj02 = true;
}
