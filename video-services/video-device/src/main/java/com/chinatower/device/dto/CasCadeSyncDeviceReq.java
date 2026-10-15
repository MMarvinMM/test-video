package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CasCadeSyncDeviceReq implements Serializable {

    /**
     * 设备编码
     */
    private String deviceCode;

    private List<CasCadeSyncDeviceChannelReq> channelList;
}
