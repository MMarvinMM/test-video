package com.chinatower.device.entity.dto;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2025/1/16 9:34
 */
@Data
public class AirPointToFlyReq {
    private String deviceCode;
    private String uavNestCode;
    private String returnMode;
    private String longitude;
    private String latitude;
    private String altitude;
    private String acceptNode;
    private String accessNode;
    private String flightRouteId;
    private String height;
}
