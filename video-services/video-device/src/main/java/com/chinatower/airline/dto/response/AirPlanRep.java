package com.chinatower.airline.dto.response;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chinatower.airline.dto.request.AirPlanSafetyOfficer;
import com.chinatower.device.entity.dto.AirTaskReq;
import com.chinatower.event.constants.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

/**
 * 计划管理表
 * @Author lize
 * @Date 2024/11/4 9:34
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AirPlanRep {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     *计划id
     */
    private String planId;

    /**
     * 计划名称
     */
    private String planName;

    /**
     * 航线id
     */
    private String flightRouteId;

    /**
     * 执行状态
     */
    private String status;

    /**
     * 任务策略
     */
    private Integer planType;

    /**
     * 无人机编码/机场编码
     */
    private String deviceCode;

    /**
     * 计划时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String planTime;

    /**
     * 机场编码
     */
    private String uavNestCode;

    /**
     * 机场名称
     */
    private String uavNestName;

    /**
     * 计划截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String planEndTime;

    /**
     * 定期执行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String regularExecutionDate;

    /**
     * 周期执行单位
     */
    private String cycleExecutionUnit;

    /**
     * 每日执行间隔
     */
    private String dayInterval;

    /**
     * 日历值
     */
    private String intervalValues;

    /**
     * 是否录制无人机图传视频
     */
    private Integer gainVideo;

    /**
     * 是否自动航线
     */
    private Integer auto;

    /**
     * 数据传输方式
     */
    private Integer gainDataMode;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 行业版本
     */
    private String appVerCode;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 删除标记位
     */
    private String deleteFlag;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 受理节点
     */
    private String acceptNode;


    /**
     * 无人机编码
     */
    private String uavCode;

    /**
     * 无人机名称
     */
    private String deviceName;

    /**
     * 航线名称
     */
    private String flightRouteName;

    /**
     * 计划任务
     */
    private List<AirTaskReq> children;

    /**
     * 媒体上传
     */
    private String mediumStatus;

    /**
     * 计划执行时间
     */
    private String scheduleExecTime;

    /**
     * 实际执行时间
     */
    private String realityTime;

    /**
     * 是否启用 0开启 1关闭
     */
    private String isItEnabled;

    private String extraField;

    private String airLineType;

    private JSONArray points;

    private JSONArray coordinates;

    /**
     * 计划开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String planStartTime;

    /**
     * 执行状态：全部执行成功、全部执行失败、全部待执行、全部执行中、部分执行成功、部分执行中、部分执行失败。根据该计划下的任务展示，展示规则如下。
     */
    private String executionStatus;

    /**
     * 任务状态个数
     */
    private HashMap<String,Integer> executionStatesCount;

    /**
     * 执行时间
     */
    private String cycleExecutionTime;

    private List<String> cycleExecutionTimes;

    private String uavAllowed;

    /**
     * 航线快照id
     */
    private String snapshotId;

    /**
     * 是否支持断点续飞
     */
    private Boolean breakPoint;

    private String definition;

    private String taskType = Constants.ZERO;

    /**
     * 最新实际飞行时间
     */
    private String lastExcTime;

    /**
     * 是否开启自动断点续飞
     */
    private Boolean autoResume;

    /**
     * 起始机巢编码
     */
    private String startNestName;
    /**
     * 降落机巢编码
     */
    private String endNestName;
    /**
     * 是否蛙跳：0-否，1-是
     */
    private Integer frogLeap;

    /**
     * 风险短信推送开关，1:开，0:关
     */
    private Integer riskMsgSwitch;

    /**
     * 安全员列表
     */
    private List<AirPlanSafetyOfficer> safetyOfficers;


}
