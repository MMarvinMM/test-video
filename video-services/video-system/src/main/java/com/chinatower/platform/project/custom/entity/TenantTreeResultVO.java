package com.chinatower.platform.project.custom.entity;

import com.chinatower.platform.framework.web.domain.SystemBaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 行政区划客户树返回vo
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TenantTreeResultVO extends SystemBaseEntity {
    private String tenantId;
    private String tenantName;
    private String tenantManager;
    private String isOperation;
    private String code;
    private String name;
    private String level;
    private String parentCode;
    private List<TenantTreeResultVO> list;
}
