package com.chinatower.event.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程视图模板实体
 * @author chmod
 * @version 1.0
 * @date 2022/1/25 14:17
 */
@Data
@TableName("vm_flow_template")
public class FlowTemplate extends BaseIndustryEntity implements Serializable {
    private static final long serialVersionUID = 2450753742134691813L;

    /** 主键 */
    @TableId(value = "template_id", type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long templateId;
    /** 模板JSON */
    private String flowJson;
    /** 模板名称 */
    private String flowName;
    /** 流程类型：1-默认流程，0-租户流程 */
    private String flowType;
    /** 默认告警来源 */
    private String initSource;
    /** 状态：1生效 0失效 */
    private String status;
}
