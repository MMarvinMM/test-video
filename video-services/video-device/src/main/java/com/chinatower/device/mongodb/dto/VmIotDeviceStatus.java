package com.chinatower.device.mongodb.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 设备状态实体
 * @author tianhao
 */
@Data
public class VmIotDeviceStatus implements Serializable {

    //设备编码
    private String deviceCode;

    //设备状态 0：上线， 1： 下线
    private String onlineStatus;

    //更新时间
    private String updateTime;
}
