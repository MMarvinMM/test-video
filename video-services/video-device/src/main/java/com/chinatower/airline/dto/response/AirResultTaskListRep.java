package com.chinatower.airline.dto.response;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2025/4/2 16:01
 */
@Data
public class AirResultTaskListRep {
    private String taskRecordId;
    private String taskName;
    private String accessNode;
    private String startTime;
    private String airLineName;
}
