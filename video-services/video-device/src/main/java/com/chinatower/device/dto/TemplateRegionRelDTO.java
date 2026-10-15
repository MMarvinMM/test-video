package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 告警模板、告警区域关联实体类
 */
@Data
public class TemplateRegionRelDTO implements Serializable {

    /**
     * 告警模板ID
     */
    private Long templateId;

    /**
     * 告警区域ID
     */
    private Long regionId;

    /**
     * 客户ID
     */
    private String tenantId;

    /**
     * 应用编码
     */
    private String industryCode;
}
