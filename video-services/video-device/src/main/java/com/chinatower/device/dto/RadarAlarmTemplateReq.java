package com.chinatower.device.dto;


import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.device.entity.vo.RadarAlarmRegionVo;
import com.chinatower.device.entity.vo.RadarAlarmBlackWhiteVO;
import com.chinatower.device.entity.vo.TemplateRelInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RadarAlarmTemplateReq extends BaseIndustryEntity implements Serializable {

    /**
     * 告警模板ID
     */
    private Long templateId;

    /**
     * 告警模板名称
     */
    private String templateName;

    /**
     * 告警类型编码
     */
    private String alarmTypeValue;

    /**
     * 模板生效开始日期
     */
    private String validStartDate;

    /**
     * 模板生效结束日期
     */
    private String validEndDate;

    /**
     * 模板生效开始时间
     */
    private String validStartTime;

    /**
     * 模板生效结束时间
     */
    private String validEndTime;

    /**
     * 关联区域信息
     */
    private List<RadarAlarmRegionVo> unionRegionList;

    /**
     * 黑白名单信息
     */
    private List<RadarAlarmBlackWhiteVO> unionBlackWhiteList;

    /**
     * 告警模板其他相关信息
     */
    private TemplateRelInfo templateRelInfo;


}
