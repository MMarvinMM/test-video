package com.chinatower.event.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.event.entity.dto.FlowLinkAttrModel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author lyy
 * @version 1.0
 * @date 2022/08/01 10:20
 */
@Data
public class FlowTemplateModel extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = -1334520687434433624L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long flowId;
    /**
     * 租户编码
     */
    private String tenantId;
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
     * 默认流程：1-默认流程，0-租户流程
     */
    private String flowType;
    /**
     * 流程模板状态 1生效 0失效
     */
    private String status;
    //告警来源
    private String warningSource;

    //告警类型编码
    private String alarmCode;
    /** 查询开启分页：true-开启，false-关闭 */
    private boolean enablePage;
    //创建人名称
    private String creatorName;
    //修改人名称
    private String modifierName;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 修改时间
     */
    private Date updateDate;
    /** 创建时间排序 */
    private String createDateOrder;
    /** 修改时间排序 */
    private String updateDateOrder;
    /** sql排序 */
    private String orderSql;

    private Integer pageNum;
    private Integer pageSize;

    /** 流程属性集合 */
    private List<FlowLinkAttrModel> attrList;

    //行业编码
    private String industryName;
    /**
     * 环节类型
     */
    private Long realLinkType;

    /**
     * 告警流程配置Id
     */
    private String alarmFlowId;

    private Long templateId;

}
