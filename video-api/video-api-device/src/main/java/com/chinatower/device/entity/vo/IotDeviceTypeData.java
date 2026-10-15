package com.chinatower.device.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description 按类型划分物联设备信息
 * @author zhujun
 * @date 2022-11-08
 */
@Data
public class IotDeviceTypeData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备类型，运管同步，各系统和运管同步
     */
    @ApiModelProperty("设备类型，运管同步，各系统和运管同步")
    private String deviceType;

    /**
     * 设备信息
     */
    @ApiModelProperty("设备信息")
    private List<IotDeviceAreaEntity> iotDevices;

}
