package com.chinatower.device.entity.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author 90372
 */
@Data
public class AirLinePointTaskReq {
    private String taskRecordId;
    private String flightRouteId;
    private String longitude;
    private String latitude;
    private String algorithmCodes;
    private String accessNode;
    private Integer status;
    private Date executeTime;
    private String deviceCode;
}
