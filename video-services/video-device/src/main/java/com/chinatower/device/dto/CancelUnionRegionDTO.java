package com.chinatower.device.dto;

import lombok.Data;

/**
 * 取消关联区域实体类
 */
@Data
public class CancelUnionRegionDTO {

    /**
     * 告警模板状态
     */
    private String templateStatus;

    /**
     * 告警模板ID
     */
    private Long templateId;

    /**
     * 告警区域ID
     */
    private String regionId;

    /**
     * 客户ID
     */
    private String tenantId;

    /**
     * 行业应用
     */
    private String industryCode;
}
