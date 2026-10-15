package com.chinatower.sync.consumer.item;

import lombok.Data;

import java.io.Serializable;


/**
 * 设备中心job稽核个性化参数
 */
@Data
public class AuditDeviceJobParam implements Serializable{
    /**
     * 开始时间时间戳
     */
    private Long startTime;

    /**
     * 结束时间时间戳
     */
    private Long endTime;

}

