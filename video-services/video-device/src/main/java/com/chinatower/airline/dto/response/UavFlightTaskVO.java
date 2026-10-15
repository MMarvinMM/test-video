package com.chinatower.airline.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @className: AlarmJudgeVO
 * @description:
 * @author: yongqi.han
 * @date: 2022/6/21
 * @requestID 需求单号及需求名称
 */
@Data
public class UavFlightTaskVO implements Serializable {
    private static final long serialVersionUID = 1L;
    //租户id
    protected String tenantId;
    //行业编码
    protected String industryCode;
    //飞行任务ID
    protected String taskRecordId;
    //计划执行时间
    private String planExecuteTime;
    // 实际执行时间
    private String actualExecuteTime;
    //任务策略 2立即 0单次定时 1重复定时
    private String taskStrategy;
    //计划ID
    private String planId;
    // 计划名称
    private String planName;
    //航点经纬度列表
    private String waypointCoordinates;
    //航线总里程（单位：米）
    private String routeTotalDistance;
    //航线ID
    private String flightRouteId;
    //航线名称
    private String flightRouteName;
    //飞行时长（单位：秒）
    private String flightDuration;
    //无人机编码
    private String uavCode;
    //无人机名称
    private String uavName;
    //机巢编码
    private String uavNestCode;
    //机巢名称
    private String uavNestName;
    //无人机型号编码
    private String uavModelCode;
    //无人机型号
    private String uavModelName;
    //任务创建人账号
    private String taskCreator;
    //任务创建人userId
    private String taskCreatorId;

    //任务执行状态（此处为已完成）执行状态  0待执行 1执行成功 2执行失败 3执行中
    private String taskStatus;
    //记录创建时间
    private String createdAt;

    //key 告警类型 value 告警数量
    private String algorithmInfoMap;

}
