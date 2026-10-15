package com.chinatower.sync.consumer.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 场景告警租户信息DTO
 */
@Data
public class SceneAlarmTenantDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;
}

