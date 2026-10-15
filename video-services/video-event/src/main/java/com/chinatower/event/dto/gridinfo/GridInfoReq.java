package com.chinatower.event.dto.gridinfo;

import lombok.Data;

@Data
public class GridInfoReq {

    /**
     * 经纬度
     */
    private String longitude;
    private String latitude;

    /**
     * 设备经度
     */
    private String deviceLongitude;

    /**
     * 设备维度
     */
    private String deviceLatitude;
}
