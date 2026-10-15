package com.chinatower.receive.vo.dto;

import lombok.Data;

/**
 * @Author: songxinfeng
 * @Date: 2025/9/18 16:13
 */

@Data
public class LatLng {

    /**
     * 经度
     */
    private double longitude;

    /**
     * 维度
     */
    private double latitude;

    public LatLng(double latitude,double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public LatLng() {

    }
}
