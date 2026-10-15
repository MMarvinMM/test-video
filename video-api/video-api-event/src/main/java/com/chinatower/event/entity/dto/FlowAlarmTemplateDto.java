package com.chinatower.event.entity.dto;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description 告警流程配置 告警类型关联模板信息
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Data
public class FlowAlarmTemplateDto extends BaseIndustryEntity implements Serializable {
    private static final long serialVersionUID = -1334520687434433624L;

    /**
     * 模板id
     */
    private Long templateId;
    /**
     * 告警流程配置id
     */
    private Long alarmFlowId;
    /**
     * 告警来源
     */
    private String warningSource;
    /**
     * 告警类型
     */
    private String alarmType;
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
    /**
     * 标识该流程模板是否为行业默认模板   1 是 0否
     */
    private String signFlag;

    /** 行业编码 */
    private String industryCode;
    /** 版本编码 */
    private String appVerCode;

    private String gridFlag;


}
