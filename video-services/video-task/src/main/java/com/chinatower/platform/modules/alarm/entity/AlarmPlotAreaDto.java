package com.chinatower.platform.modules.alarm.entity;

import lombok.Data;

/**
 * 告警地块面积表
 */
@Data
public class AlarmPlotAreaDto {

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 省编码
     */
    private String provinceId;

    /**
     * 市编码
     */
    private String cityId;

    /**
     * 县编码
     */
    private String countyId;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 地块面积
     */
    private String plotAreaSum;

    /**
     * 行业编码
     */
    private String industryCode;

    public AlarmPlotAreaDto(String tenantId, String provinceId, String cityId, String countyId, String deviceCode, String plotAreaSum, String industryCode) {
        this.tenantId = tenantId;
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.countyId = countyId;
        this.deviceCode = deviceCode;
        this.industryCode = industryCode;
        this.plotAreaSum = plotAreaSum;
    }

}
