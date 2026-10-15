package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RelayDeviceInfo implements Serializable {

    /**
     * 设备编码
     */
    private String deviceCode;
}
