package com.chinatower.device.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * 关联区域弹框实体类
 */
@Data
public class RegionTemplateRelDTO implements Serializable {

    /**
     * 告警区域名称
     */
    private String regionName;

    /**
     * 告警类型编码
     */
    private String alarmTypeValue;

    /**
     * 告警模板ID
     */
    private Long templateId;

    /**
     * 区域ID
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

    /**
     * 告警模板状态
     */
    private String templateStatus;


}
