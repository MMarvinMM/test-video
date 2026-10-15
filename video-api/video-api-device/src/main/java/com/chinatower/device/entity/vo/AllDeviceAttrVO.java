package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AllDeviceAttrVO implements Serializable {

    private List<DeviceAttrVO> videoDeviceList;

    private List<DeviceAttrVO> radarDeviceList;

    private List<DeviceAttrVO> sensorDeviceList;

    private List<ServiceAttrVO> satelliteServiceList;

}
