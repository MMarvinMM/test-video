package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class WarningOrderResultCountVo implements Serializable {

    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 告警来源
     */
    private String warningSource;
    /**
     * 告警数量
     */
    private Integer sum;

    private String extraField;
}
