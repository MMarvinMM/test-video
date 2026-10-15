package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class LongitudeLatitudeVO {
    private String id;
    // 打点经度
    private double pointLon;

    // 打点纬度
    private double pointLat;
}
