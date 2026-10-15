package com.chinatower.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceIndustryDTO implements Serializable {
    
    /**
     * 设备编码
     */
    private String deviceCode;
    
    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 设备类型
     */
    private Integer deviceType;
}
