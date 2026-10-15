package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class VideoPtzQueryRequest {

    // 摄像机经度
    private double cameraLon;

    // 摄像机纬度
    private double cameraLat;

    // 打点经度
    private double pointLon;

    // 打点纬度
    private double pointLat;

    // 设备编码
    private String deviceCode;

    // 通道编码
    private String channelCode;
    
    // 租户id
    private String tenantId;
    
    // 用户id
    private String userId;
}
