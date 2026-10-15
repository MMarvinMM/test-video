package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 雷达告警区域简要信息实体类
 */
@Data
public class RadarRegionSelectedVO implements Serializable {

    /**
     * 已选择的雷达告警区域信息
     */
    private List<RadarRegionBriefVO> selectedRadarRegionList;

    /**
     * 未选择的雷达告警区域信息
     */
    private List<RadarRegionBriefVO> unselectedRadarRegionList;
}
