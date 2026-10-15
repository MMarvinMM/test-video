package com.chinatower.event.core.flow.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @className: FlowTemplateLinkDto
 * @description: 流程环节查询dto
 * @author: yongqi.han
 * @date: 2022/8/8
 * @requestID 需求单号及需求名称
 */
@Data
public class QueryLinkDto implements Serializable {

    private String appVerCode;
    private String industryCode;
    private String tenantId;
    private String warningSource;
    private String alarmType;

}
