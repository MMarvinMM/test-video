package com.chinatower.device.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class EnergyDeviceStationVO {

    private Long id;
    private String stationCode;
    private String stationName;
    private String stationPubCode;
    private String businessType;
    private String businessTypeName;
    private String provinceId;
    private String provinceName;
    private String cityId;
    private String cityName;
    private String countyId;
    private String countyName;
    private String address;
    private Double lat;
    private Double lon;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;
}
