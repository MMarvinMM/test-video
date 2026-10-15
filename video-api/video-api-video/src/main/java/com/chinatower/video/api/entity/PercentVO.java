package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class PercentVO {
    private String id;
    private String percentU;
    private String percentV;
    // 打点经度
    private double pointLon;
    // 打点纬度
    private double pointLat;
}
