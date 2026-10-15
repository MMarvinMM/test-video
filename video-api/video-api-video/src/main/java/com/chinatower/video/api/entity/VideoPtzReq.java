package com.chinatower.video.api.entity;

import lombok.Data;

import java.util.List;

@Data
public class VideoPtzReq {
    // 设备编码
    private String deviceCode;

    // 通道编码
    private String channelCode;

    //中心点经纬度
    private LongitudeLatitudeVO centerPoint;

    //顶点经纬度
    private List<LongitudeLatitudeVO> vertexList;

    private String tenantId;
}
