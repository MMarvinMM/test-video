package com.chinatower.airline.dto.response;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2025/4/16 10:00
 */
@Data
public class AirTaskStatisticsRes {
    private String uavName;
    private String taskName;
    private String taskStatus;
    private String planTime;
    private String taskId;
    private String flightMainType;
}
