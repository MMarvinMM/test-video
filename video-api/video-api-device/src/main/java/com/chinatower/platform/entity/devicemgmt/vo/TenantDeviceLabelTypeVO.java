package com.chinatower.platform.entity.devicemgmt.vo;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

@Data
public class TenantDeviceLabelTypeVO extends BaseIndustryEntity {
    private static final long serialVersionUID = -3201609084635922393L;
    private Long id;
    private String labelTypeName;
}
