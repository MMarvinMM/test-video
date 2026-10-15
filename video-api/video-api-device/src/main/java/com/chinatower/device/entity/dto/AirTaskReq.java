package com.chinatower.device.entity.dto;

import com.chinatower.common.core.dto.PageDTO;
import com.chinatower.platform.constants.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author lize
 * @Date 2024/11/5 20:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirTaskReq extends PageDTO {

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
    private String status;

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
//     * 删除标识 1假删除 0未删除  2：真删除
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
     * 任务名称
     */
    private String planName;

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

    /**
     * 任务策略
     */
    private Integer planType;

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
     * 任务飞行时长
     */
    private String flyTime;
    /**
     * 任务执行失败原因
     */
    private String errorReason;
    /**
     * 断点点位json
     */
    private String breakPointJson;

    private String originalTaskRecordIds;

    private String uavCode;

    private String taskType = Constants.ZERO;

    /**
     * 累计里程
     */
    private String accumulateMileage;

    /**
     * 是否开启自动断点续飞
     */
    private Boolean autoResume;

    /**
     * 起始机巢编码
     */
    private String startNestCode;
    /**
     * 降落机巢编码
     */
    private String endNestCode;

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
