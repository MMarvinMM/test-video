package com.chinatower.platform.project.custom.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class TenantLocationReqVO implements Serializable {

    /**
     * 客户编码
     */
    private String tenantId;

    /**
     * 节点编码
     */
    private String locationCode;

    private String industryCode;

    private Integer pageNum;

    private Integer pageSize;
}
