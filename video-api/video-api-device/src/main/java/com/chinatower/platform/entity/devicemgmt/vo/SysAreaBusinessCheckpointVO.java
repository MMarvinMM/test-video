package com.chinatower.platform.entity.devicemgmt.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 行政区域对象 sys_area
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class SysAreaBusinessCheckpointVO extends BaseDataScopeEntity {
    private static final long serialVersionUID = 1L;

    private String areaCode;

    private String areaName;

    private String areaLevel;

    private String areaParentCode;

    private String areaParentName;

    private String provinceId;

    private String cityId;

    private String countyId;



}
