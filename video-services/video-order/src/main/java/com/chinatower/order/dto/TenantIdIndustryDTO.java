package com.chinatower.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TenantIdIndustryDTO implements Serializable {

    /** 租户ID */
    private String tenantId;

    /** 行业编码 */
    private String industryCode;
}
