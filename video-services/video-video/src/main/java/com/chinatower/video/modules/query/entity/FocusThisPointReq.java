package com.chinatower.video.modules.query.entity;

import lombok.Data;

@Data
public class FocusThisPointReq {
    // 打点经度
    private double pointLon;

    // 打点纬度
    private double pointLat;

    // 租户ID
    private String customerCode;

    // 聚焦距离参数
    private int focusLimit = 3000;
}
