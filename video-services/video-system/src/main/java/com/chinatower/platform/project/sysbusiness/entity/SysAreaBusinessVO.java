package com.chinatower.platform.project.sysbusiness.entity;

import com.chinatower.common.annotation.Excel;
import com.chinatower.platform.framework.web.domain.SystemBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 行政区域对象 sys_area
 *
 * @author zhaol
 * @date 2022-02-17
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class SysAreaBusinessVO extends SystemBaseEntity {
    private static final long serialVersionUID = 1L;

    private String areaCode;

    private String areaName;

    private String areaLevel;

    private String areaParentCode;

    private String areaParentName;

    private String provinceId;

    private String cityId;

    private String countyId;

    private String townId;

}
