package com.chinatower.event.api.model;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2025/1/7 17:35
 */
@Data
public class AlarmByTaskReq {
    String taskBatchId;
    String platFormCode;
    String toPlatformCode;
    String type;
}
