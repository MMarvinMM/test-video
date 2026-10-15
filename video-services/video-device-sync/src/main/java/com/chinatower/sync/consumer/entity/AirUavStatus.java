package com.chinatower.sync.consumer.entity;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2025/2/7 10:45
 */
@Data
public class AirUavStatus {
    private String accessNode;
    private String acceptNode;
    private String deviceCode;
    private String deviceName;
    private String deviceType;
    private int deviceStatus;
    private int runStatus;
    private String alarmMsgId;
    private String alarmTitle;
    private String alarmSignalId;
    private String alarmSourceCode;
    private int alarmStatus;
    private long reportTime;
    private String createTime;
    private String originalTaskRecordId;
    private String taskRecordId;
}
