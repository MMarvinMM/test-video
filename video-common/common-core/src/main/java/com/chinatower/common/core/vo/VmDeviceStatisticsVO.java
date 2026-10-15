package com.chinatower.common.core.vo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @className: DeviceStatisticsVO
 * @description: 设备未完结告警数量统计信息
 * @author: lyy
 * @date: 2025-01-10 10:37
 * @version v1.7.20
 */
@Data
public class VmDeviceStatisticsVO {

    /**
     * 设备编码
     */
    @Field("device_code")
    private String deviceCode;

    /**
     * 租户编码
     */
    @Field("tenant_id")
    private String tenantId;

    /**
     * 行业编码
     */
    @Field("industry_code")
    private String industryCode;

    /**
     * 未完结告警数量
     */
    private Integer orderUnCompleteNum;

    /**
     * 日期：年月
     */
    private String date;

    /**
     * 是否是未完结告警标识（0-否，1-是）
     */
    private String isCompleteFlag;
}
