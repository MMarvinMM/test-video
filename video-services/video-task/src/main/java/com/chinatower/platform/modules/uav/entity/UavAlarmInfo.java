package com.chinatower.platform.modules.uav.entity;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2025/2/19 15:25
 */
@Data
public class UavAlarmInfo {
    private String id;
    private String deviceCode;
    private String uavCode;
    private String uavName;
    private String type;
    private String status;
    private String unConnectTime;
    private String sendTime;
    private String sendUserId;
    private String latitude;
    private String longitude;
    private String address;
    private String tenantId;
    private String industryCode;
    private String createUser;
    private String createTime;
}
