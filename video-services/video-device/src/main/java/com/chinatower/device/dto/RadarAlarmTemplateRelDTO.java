package com.chinatower.device.dto;


import com.chinatower.device.entity.vo.RadarAlarmBlackWhiteVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 告警模板关联区域、白名单实体类
 */
@Data
public class RadarAlarmTemplateRelDTO implements Serializable {


    /**
     * 告警模板ID
     */
    private Long templateId;

    /**
     * 白名单信息
     */
    private List<RadarAlarmBlackWhiteVO> whiteInfoList;

    /**
     * 关联区域信息
     */
    private List<RegionTemplateRelDTO> regionInfoList;
}
