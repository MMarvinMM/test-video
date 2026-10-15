package com.chinatower.sync.consumer.entity;

import lombok.Data;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2025/2/18 17:46
 */
@Data
public class AIrUavConnectionInfo {
    private String deviceCode;
    private String uavCode;
    private String uavName;
    private String lostContact;
    private String latitude;
    private String longitude;
    private String altitude;
    private String address;
    private String status;
    private long timestamp;
    private String timestampStr;
    private String taskRecordId;
    private String acceptNode;
}
