package com.chinatower.event.entity.dto;


import lombok.Data;

import java.io.Serializable;

@Data
public class ReceiveIotAlarmFactorMongoDto implements Serializable {

    /**
     * 物联因子code
     */
    private String factorCode;

    /**
     * 瞬时时间
     */
    private String mergeTime;

    /**
     * 物联设备编码
     */
    private String deviceCode;

    /**
     * 物联设备监测值
     */
    private String monitorValue;
}
