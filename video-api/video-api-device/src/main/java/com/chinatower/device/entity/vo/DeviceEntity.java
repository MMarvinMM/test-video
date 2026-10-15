package com.chinatower.device.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeviceEntity {

    @ApiModelProperty(value = "设备编码")
    private String cameraCode;

    @ApiModelProperty(value = "设备名称")
    private String cameraName;

    @ApiModelProperty(value = "设备状态:0在线，1离线")
    private String cameraStatus;

    @ApiModelProperty(value = "经度")
    private String cameraLongitude;

    @ApiModelProperty(value = "纬度")
    private String cameraLatitude;

    @ApiModelProperty(value = "地址")
    private String cameraLocation;

    @ApiModelProperty(value = "通道编码")
    private String channelCode;

    @ApiModelProperty(value = "通道名称")
    private String channelName;

}
