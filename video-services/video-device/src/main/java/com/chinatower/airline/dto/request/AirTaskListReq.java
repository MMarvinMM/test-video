package com.chinatower.airline.dto.request;

import com.chinatower.common.core.dto.PageDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2025/1/16 15:18
 */
@Data
public class AirTaskListReq extends PageDTO {
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 计划id
     */
    private String planId;

    /**
     * 飞行任务id
     */
    private String taskRecordId;

    /**
     * 计划执行时间
     */
    private String scheduleExecTime;

    /**
     * 实际执行时间
     */
    private String realityTime;
    /**
     * 执行状态
     */
    private List<String> status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 修改人
     */
    private String updateUser;

//    /**
//     * 删除标识 1删除 0未删除
//     */
//    private String deleteFlag;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 受理节点
     */
    private String acceptNode;

    /**
     * 计划名称
     */
    private String planName;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 机场名称
     */
    private String uavNestName;

    /**
     * 机场编码
     */
    private String uavNestCode;

    /**
     * 无人机设备code
     */
    private String deviceCode;

    /**
     * 无人机名称
     */
    private String deviceName;

    /**
     * 航线名称
     */
    private String flightRouteName;
    /**
     * 航线id
     */
    private String flightRouteId;

    /**l
     * 任务策略
     */
    private List<Integer> planType;

    /**
     * 媒体上传
     */
    private String mediumStatus;

    /**
     * 执行状态：全部执行成功、全部执行失败、全部待执行、全部执行中、部分执行成功、部分执行中、部分执行失败。根据该计划下的任务展示，展示规则如下。
     */
    private String executionStatus;

    /**
     * 是否启用 0开启 1关闭
     */
    private String isItEnabled;

    /**
     * 航线快照id
     */
    private String snapshotId;

    /**
     *
     */
    private String queryTaskStartTime;

    /**
     *
     */
    private String queryTaskEndTime;

    /**
     * 无人机设备code集合
     */
    private List<String> deviceCodes;

    /**
     * 任务类型 0 1复飞
     */
    private List<String> taskType;

    /**
     * 计划时间
     */
    private String realityStartTime;

    /**
     * 计划时间
     */
    private String realityEndTime;

    private String uavCode;
/*
    *//**
     *  1:查询包含假删的数据   其他：未删除的有效数据
     *//*
    private String deleteFlag;*/

    /**
     * 是否实际飞行里程 SHOW展示，空不展示
     */
    private String showAirMileage;


    /**
     * 是否开启自动断点续飞
     */
    private Boolean autoResume;

    /**
     * 是否蛙跳：0-否，1-是
     */
    private Integer frogLeap;

    /**
     * 起始机巢
     */
    private String startNestName;

    /**
     * 降落机巢
     */
    private String endNestName;
}
