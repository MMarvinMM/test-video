package com.chinatower.device.dataobject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceChannelInfo implements Serializable {
    private String deviceCode;

    private String deviceName;

    private String channelCode;

    private String channelName;

    private String longitude;

    private String latitude;

    private String siteCode;
}
