package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 雷达告警模板列表实体类
 */
@Data
public class RadarAlarmTemplateVO extends BaseIndustryEntity implements Serializable {

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
     * 告警模板状态 1-开启 0-关闭
     */
    private String templateStatus;

    /**
     * 告警类型名称
     */
    private String alarmTypeName;

    /**
     * 关联区域数量
     */
    private String unionRegionCount;

    /**
     * 客户名称
     */
    private String tenantName;

    /**
     * 行业应用名称
     */
    private String industryName;

    /**
     * 行业应用版本
     */
    private String appVerCode;

    /**
     * 关联告警区域相关信息
     */
    private List<RadarAlarmRegionVo> unionRegionList;


    /**
     *模板生效开始日期
     */
    private String validStartDate;

    /**
     *模板生效结束日期
     */
    private String validEndDate;

    /**
     *模板生效开始时间
     */
    private String validStartTime;

    /**
     *模板生效结束时间
     */
    private String validEndTime;

    /**
     * 模板属性信息
     */
    private String templateJson;

    /**
     * 黑白名单信息
     */
    private List<RadarAlarmBlackWhiteVO> unionBlackWhiteList;

    /**
     * 告警模板其他相关信息
     */
    private TemplateRelInfo templateRelInfo;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 同步状态 同步状态 0-初始值 1-成功 2-失败
     */
    private Integer syncStatus;

}
