package com.chinatower.platform.entity.devicemgmt.vo;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

/**
 * 租户标签配置实体
 * @author chmod
 * @version 1.0
 * @date 2022/1/5 14:25
 */
@Data
public class TenantLabelConfigVO extends BaseIndustryEntity {
    private static final long serialVersionUID = -7570911702788464100L;
    private Long labelId;
    private String tenantId;
    private String labelName;

    private Long labelTypeId;

    public TenantLabelConfigVO(String tenantId, String labelName) {
        this.tenantId = tenantId;
        this.labelName = labelName;
    }

    public TenantLabelConfigVO(Long labelId, String labelName) {
        this.labelId = labelId;
        this.labelName = labelName;
    }
}
