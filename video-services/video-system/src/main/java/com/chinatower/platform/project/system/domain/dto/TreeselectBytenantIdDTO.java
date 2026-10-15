package com.chinatower.platform.project.system.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TreeselectBytenantIdDTO implements Serializable {

    /** 租户ID */
    private String tenantId;

    /** 行业编码 */
    private String industryCode;

    /** 状态 */
    private String status;
}
