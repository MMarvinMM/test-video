package com.chinatower.platform.modules.uav.entity;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2025/7/22 18:03
 */
@Data
public class AirLinePhotoTask {
    private String id;
    private String compareId;
    private String compareStatus;
    private String compareDate;
    private String taskRecordId;
    private String compareTaskRecordId;
    private String latitude;
    private String longitude;
    private String deviceCode;
    private String accessNode;
    private String alarmCode;
    private String tenantId;
    private String industryCode;
    private String flightRouteId;
    private String algorithmCode;
}
