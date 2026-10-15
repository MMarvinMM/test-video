package com.chinatower.common.core.dto;

import lombok.Data;

@Data
public class DeviceAndfunction {

    //设备id
    private String deviceId;
    //订购方法id
    private String functionId;
    //累计费用
    private long amount;
}
