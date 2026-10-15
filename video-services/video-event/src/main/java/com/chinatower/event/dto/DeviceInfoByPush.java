package com.chinatower.event.dto;

import lombok.Data;

@Data
public class DeviceInfoByPush {

    private String countyId;

    private String regionName;

    private String location;

    private String latitude;

    private String longitude;

    private String deviceCode;
}
