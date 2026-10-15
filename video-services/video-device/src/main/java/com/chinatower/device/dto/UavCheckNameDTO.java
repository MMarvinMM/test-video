package com.chinatower.device.dto;

import lombok.Data;

@Data
public class UavCheckNameDTO {

    private String tenantId;

    private String industryCode;

    private String deviceCode;

    private String deviceName;
}
