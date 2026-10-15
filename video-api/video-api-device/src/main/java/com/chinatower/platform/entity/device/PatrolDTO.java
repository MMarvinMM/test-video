package com.chinatower.platform.entity.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 巡航计划
 * Created by zheng.yan on 2022/1/22.
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatrolDTO {
    /**
     * 订购编码
     */
    private String orderCode;

    /**
     * 客户编码
     */
    private String tenantId;

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 巡航路径id
     */
    private Long patrolId;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 预置点列表
     */
    private List<PresetDTO> presets;

    /**
     * 巡航速度 0-全速； 1-1/2倍速； 2-1/4倍速； 3-1/8倍速； 4-1/16倍速；
     */
    private Long speed;

    /**
     * 巡航名称
     */
    private String patrolName;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 巡航控制类型 1-停用；2-启用
     */
    private int patrolType;

    private List<Long> patrolIds;


    private String creator;

    private String creatorDate;

    private String modifier;

    private String modifierDate;

    private String deleteFlag;

    private List<PatrolDTO> patrolList;

    private List<String> devIdList;

    private Long presetId;

    /**
     * 巡航计划时间段
     */
    private String[] startTimePeriodArr;
    /**
     * 巡航计划时间段
     */
    private String startTimePeriod;
    /**
     * 预约启动状态：1-停用；2-启用；
     */
    private String startStop;
    /**
     * 巡航启动状态：1-停用；2-启用
     */
    private String ifCruise;
    /**
     * 预约启动时间
     */
    private String startStopDate;

    //行业编码
    private String industryCode;

    //行业版本编码
    private String appVerCode;

}
