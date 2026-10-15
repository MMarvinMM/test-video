package com.chinatower.video.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.util.List;

@Data
public class VideoPatrolVO extends BaseDataScopeEntity {

    private long id;

    //订购编码
    private String orderCode;

    //客户编码
    private String customerCode;

    //用户编码
    private String userCode;

    //巡航路径id
    private Long patrolId;

    //设备编码
    private String deviceCode;

    //通道编码
    private String channelCode;

    //通道名称
    private String channelName;

    //预置点列表
    //private List<PresetVO> presets;

    //巡航速度 0-全速； 1-1/2倍速； 2-1/4倍速； 3-1/8倍速； 4-1/16倍速；
    private Long speed;

    //巡航名称
    private String patrolName;

    //摄像机名称
    private String deviceName;

    //执行状态（执行中/未执行）
    private String executionState;

    //开始时间
    private String startTime;

    //结束时间
    private String endTime;

    //开始时间(格式：yyyy-MM-dd）
    private String createStartTime;

    //结束时间(格式：yyyy-MM-dd）
    private String createEndTime;

    //开始时间(格式：yyyy-MM-dd）
    private String updateStartTime;

    //结束时间(格式：yyyy-MM-dd）
    private String updateEndTime;

    //巡航控制类型 1-停用；2-启用
    private int patrolType;

    private long[] patrolIds;


    private String creator;

    private String creatorDate;

    private String modifier;

    private String modifierDate;

//    private String deleteFlag;

    private List<VideoPatrolVO> patrolVOList;

    //订单状态：0变更;1新增订购;2订单续订、启用;3退订;4停用、失效
    private int ordStatus;

    private List<String> devIdList;

    private long presetId;

    //巡航计划时间段
    private String[] startTimePeriodArr;
    //巡航计划时间段
    private String startTimePeriod;
    //预约启动状态：1-停用；2-启用；
    private String startStop;
    //巡航启动状态：1-停用；2-启用
    private String ifCruise;
    //巡航启动状态：1-停用；2-启用
    private String isCruise;
    //预约启动时间
    private String startStopDate;

    private String industryCode;

    //更新开始日期
    private String updateStartDate;

    //更新结束日期
    private String updateEndDate;

    //createTime updateTime
    private String orderBy;

    //asc desc
    private String sort;

    //巡航路径id列表
    private List<Long> patrolIdList;
}
