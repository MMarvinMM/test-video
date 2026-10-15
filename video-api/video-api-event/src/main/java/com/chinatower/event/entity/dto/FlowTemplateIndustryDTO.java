package com.chinatower.event.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author tianhao
 * @version 1.0
 * @date 2022/7/28
 */
@Data
@ApiModel(description = "告警流程配置-新增默认初始化流程关联告警类型信息")
public class FlowTemplateIndustryDTO extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = -1334520687434433624L;
    /**
     * 主键
     */
    private Long id;

    /**
     * 模板ID
     * @required true
     */
    private String templateId;

    /**
     * 默认的流程模板ID
     * @required true
     */
    private String industryTemplateId;

    /**
     * 告警来源
     * @required true
     */
    private String warningSource;

    /**
     *  状态：0-默认不是初始化模板、1-默认初始化模板
     *  @required false
     */
    private String status;

    /**
     * 流程环节配置
     * @required true
     */
    private List<FlowLinkIndustryConfigDO> configList;

    /**
     * 属地版本
     */
    private String localVersion;

}
