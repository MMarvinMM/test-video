package com.chinatower.event.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lyy
 * @version 1.0
 * @date 2022/7/18 14:20
 */
@Data
@ApiModel(description = "告警流程配置-查询默认初始化流程")
public class FlowTemplateDTO extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = -1334520687434433624L;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long templateId;
    /**
     * 租户名称
     */
    private String tenantName;
    /**
     * 流程模板名称
     */
    private String flowName;
    /**
     * 流程模板前端展示json 详见antv-g6
     * id 元素标识 style元素样式 type元素类型 label元素文本标签
     * nodes 节点数据
     * edges 连线数据  自定义边 起始节点startPoint 结束节点endPoint
     */
    private String flowJson;
    /**
     * 流程类型：1默认流程，0租户流程
     */
    private String flowType;
    /**
     * 默认告警来源
     */
    private String initSource;
    /**
     * 流程模板状态：1生效，0失效
     */
    private String status;
    /**
     * 创建人
     */
    private String creatorName;
    /**
     * 修改人
     */
    private String modifierName;

    /**
     * 分页参数
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Integer pageNum;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Integer pageSize;

    /** 创建时间排序 */
    private String createDateOrder;
    /** 修改时间排序 */
    private String updateDateOrder;
    /** sql排序 */
    private String orderSql;

}
