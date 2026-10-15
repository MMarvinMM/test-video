package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceAccessNodeVO implements Serializable {

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 接入节点
     */
    private String accessNode;
}