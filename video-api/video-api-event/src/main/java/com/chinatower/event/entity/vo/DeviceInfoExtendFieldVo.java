package com.chinatower.event.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DeviceInfoExtendFieldVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 物联设备编号
     */
    private String iotDeviceCode;

    /**
     * 联动摄像机信息
     */
    private List<IotRelDeviceVo> relDevice;
}
