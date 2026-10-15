package com.chinatower.event.entity.dto;

import lombok.Data;

@Data
public class GridInfoReq {

    /**
     * 经度
     * @required true
     */
    private String longitude;

    /**
     * 纬度
     * @required true
     */
    private String latitude;

    /**
     * 设备经度
     * @required true
     */
    private String deviceLongitude;

    /**
     * 设备维度
     * @required true
     */
    private String deviceLatitude;
}
