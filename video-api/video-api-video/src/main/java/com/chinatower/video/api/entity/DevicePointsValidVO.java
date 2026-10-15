package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class DevicePointsValidVO {

    private String deviceCode;

    //0:未标定  1已标定
    private String isPointsValid;

}
