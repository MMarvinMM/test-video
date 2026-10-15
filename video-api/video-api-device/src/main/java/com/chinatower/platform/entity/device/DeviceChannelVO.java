package com.chinatower.platform.entity.device;

import lombok.Data;

@Data
public class DeviceChannelVO {

    private String deviceCode;
    private String channelCode;
    private String channelName;

    private String triggerRule;
}
