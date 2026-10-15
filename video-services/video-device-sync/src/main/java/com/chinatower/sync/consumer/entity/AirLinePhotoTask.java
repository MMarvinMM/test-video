package com.chinatower.sync.consumer.entity;

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
    private String lastCycleDate;
}
