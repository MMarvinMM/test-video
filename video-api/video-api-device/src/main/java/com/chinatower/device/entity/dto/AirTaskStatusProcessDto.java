package com.chinatower.device.entity.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author cuibaoluo
 * @date 2025/12/17
 * @description：
 */
@Data
public class AirTaskStatusProcessDto {

    private String taskRecordId;

    private String deviceCode;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;

    private String execStatus;

    private String mediaSyncStatus;

    private String acceptNode;

    private String flyTime;

    private String errorReason;

    private String breakPointReqJson;

    private String waypointIndex;

    private String waypointState;

}
