package com.chinatower.manage.api.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 流程模板关联告警类型实体
 * @author lyy
 * @version 1.0
 * @date 2022/7/26 14:17
 */
@Data
public class LocalizationFlowTemplateIndustryDO extends BaseIndustryEntity implements Serializable {
    private static final long serialVersionUID = 2450753742134691813L;
    /** 主键 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /** 模板id */
    private Long templateId;
    /** 告警来源 */
    private String warningSource;
    /** 状态： 0默认不是初始化模板 1默认初始化模板 */
    private String status;

    private String localVersion;
}
