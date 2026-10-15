package com.chinatower.airline.dto.response;

import lombok.Data;

/**
 * @author csj
 * @description
 * @date 2024/11/4 19:58
 */
@Data
public class AirDeviceInfoBaseRep {

    /**
     * 基站名称
     * */
    private String name;
    /**
     * 纬度
     * */
    private Double latitude;
    /**
     * 经度
     * */
    private Double longitude;

    /**
     * 海拔
     * */
    private Double altitude;

    /**
     * 地址
     * */
    private String address;
}
