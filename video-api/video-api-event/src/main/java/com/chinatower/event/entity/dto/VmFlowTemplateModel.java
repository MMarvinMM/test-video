package com.chinatower.event.entity.dto;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description 告警流程模板实体对象 新表对应的实体对象
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Data
public class VmFlowTemplateModel extends BaseIndustryEntity implements Serializable {
    private static final long serialVersionUID = -1334520687434433624L;

    /**
     * 模板id
     */
    private Long templateId;
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
     * 流程模板状态 1生效 0失效
     */
    private String status;
}
