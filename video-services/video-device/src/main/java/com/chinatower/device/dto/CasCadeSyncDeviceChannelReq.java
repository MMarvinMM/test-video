package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CasCadeSyncDeviceChannelReq implements Serializable {

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道编码别名
     */
    private String channelAlia;

    /**
     * 组织编码
     */
    private String orgCode;
}
