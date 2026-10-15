package com.chinatower.device.dto;

import lombok.Data;

@Data
public class RoleDataRelVO {
    private Long roleId;

    private String dataScope;

    private String tenantId;

    private String industryCode;

    private String appVerCode;
}
