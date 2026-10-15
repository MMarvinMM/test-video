package com.chinatower.event.entity.dto;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lyy
 * @version 1.0
 * @date 2022/7/20 10:20
 */
@Data
@ApiModel(description = "告警流程配置新增入参")
public class VmAlarmFlowConfigModel extends BaseIndustryEntity implements Serializable {
    private static final long serialVersionUID = -6966106571145117171L;
    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 告警流程配置名称
     */
    private String alarmFlowName;
    /**
     * 自定义租户流程ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long templateId;
    /**
     * 流程名称
     */
    private String flowName;
    /**
     * 状态：1-生效、0-失效
     */
    private String status;
    /**
     * 流程模板关联告警类型集合
     * @required true
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<VmAlarmFlowTemplateTypeDO> flowTemplateTypeList;
    private List<String> sourceAndTypeList;
    /**
     * 流程环节属性集合
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<VmFlowLinkAttrPageDTO> flowLinkAttrList;

    /**
     * ”0“：默认流程   ”1“：自定义流程
     */
    private String configType;

    /**
     * 操作类型
     */
    private String operType;
}
