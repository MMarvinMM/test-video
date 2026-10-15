package com.chinatower.device.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

/**
 * @program: video-platform
 * @ClassName: SysAreaBusinessWaterLevel
 * @description: 行政区域对象
 * @author: fengty
 * @create: 2024-12-13 09:56
 */
@Data
public class SysAreaBusinessWaterLevel extends BaseDataScopeEntity {

    private String areaCode;

    private String areaName;

    private String areaLevel;

    private String areaParentCode;

    private String areaParentName;

    private String provinceId;

    private String cityId;

    private String countryId;
}
