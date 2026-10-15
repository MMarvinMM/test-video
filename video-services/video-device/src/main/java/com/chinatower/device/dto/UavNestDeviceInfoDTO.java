package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UavNestDeviceInfoDTO implements Serializable {

    private String deviceCode;

    private String deviceName;

    private String resourceStatus;

    private String manufacturerCode;

    private String modelCode;

    private String altitude;

    private String uavNestManufacturerName;

    private String uavNestModelName;

    private String uavNestStatusName;

    private Integer uavNestPositionFix;

    private Integer uavNestCalibrationStatus;

    private String uavNestProvinceCode;

    private String uavNestCityCode;

    private String uavNestCountyCode;

    private String uavNestTownCode;

    private String uavNestLongitude;

    private String uavNestLatitude;
}
