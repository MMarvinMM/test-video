package com.chinatower.system.api.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class TenantInfoVO implements Serializable {

    private String tenantId;

    private String locationCode;

    private String industryCode;

    private String appVerCode;

    private String operationModifier;
}
