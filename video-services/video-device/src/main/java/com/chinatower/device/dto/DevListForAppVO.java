package com.chinatower.device.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author lzt
 * @version 1.0
 * @date 2022/08/26
 */
@Data
@ToString
public class DevListForAppVO implements Serializable {
    private static final long serialVersionUID = -2191999855013830554L;

    @ApiModelProperty(value = "设备编号")
    private String deviceCode;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "设备状态 0-在线 1-离线")
    private String deviceStatus;

    @ApiModelProperty(value = "位置")
    private String location;

}