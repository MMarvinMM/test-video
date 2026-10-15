package com.chinatower.event.dataobject;

import com.baomidou.mybatisplus.annotation.*;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.common.core.web.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 告警流程配置表
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vm_alarm_flow_config")
public class VmAlarmFlowConfigDO extends BaseIndustryEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 告警流程配置名称
     */
    private String alarmFlowName;

    /**
     * 模板名称
     */
    private String flowName;

    /**
     * 自定义租户流程ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long templateId;

    /**
     * 1生效 0失效
     */
    private String status;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 创建人名称
     */
    private String creatorName;

    /**
     * 修改人名称
     */
    private String modifierName;

    @TableField(exist = false)
    private String configType;
}
