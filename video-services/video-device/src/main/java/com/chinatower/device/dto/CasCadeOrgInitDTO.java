package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CasCadeOrgInitDTO implements Serializable {

    private String tenantId;

    private String platCode;

    private String deviceCode;

    private String provinceId;
    private String cityId;
    private String countyId;

    private String provinceName;
    private String cityName;
    private String countyName;

    private String orgCode;

    private String orgName;

    private String parentOrgCode;
}
