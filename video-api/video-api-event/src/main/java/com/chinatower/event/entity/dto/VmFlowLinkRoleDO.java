package com.chinatower.event.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 流程环节角色表
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vm_flow_link_role")
public class VmFlowLinkRoleDO extends BaseIndustryEntity implements Serializable {
    private static final long serialVersionUID = 4182320090573659121L;

    /** 主键 */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long linkRoleId;
    /** 角色ID */
    private String roleId;
    /** 组织ID */
    private String orgId;
    /** 模板ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long templateId;
    /** 环节ID */
    private Integer linkId;
    /** 流程ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long attrId;
    /** 网格员适用类型标识 默认0  0正式 1网格 */
    private String gridSign;
}
