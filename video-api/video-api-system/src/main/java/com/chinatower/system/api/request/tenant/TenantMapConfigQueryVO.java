package com.chinatower.system.api.request.tenant;

import lombok.Data;

import java.io.Serializable;

@Data
public class TenantMapConfigQueryVO implements Serializable {

    private String mapType;

    private String tenantId;

    private String acceptNode;

    private String industryCode;

    private String appVerCode;
}
