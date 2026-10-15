package com.chinatower.device.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author lize
 * @Date 2024/11/5 18:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirPlanAddOrEditReq {

    /**
     * 巡查计划id
     */
    private Integer planId;

    /**
     * 巡查计划名称
     */
    private String name;

    /**
     * 航线id
     */
    private String flightRouteId;

    /**
     * 数据传输方式
     */
    private Integer gainDataMode;

    /**
     * 是否录制无人机图传视频
     */
    private Integer gainVideo;

    /**
     * 是否自动航线
     */
    private Integer auto;

    /**
     * 计划类型
     */
    private Integer planType;

    /**
     * 计划开始时间
     */
    private String startTime;

    /**
     * 计划截止时间
     */
    private String endTime;

    /**
     * 定期执行时间
     */
    private String regularExecutionDate;

    /**
     * 周期执行单位
     */
    private Integer cycleExecutionUnit;

    /**
     * 每日执行间隔
     */
    private Integer dayInterval;

    /**
     * 日历值
     */
    private Integer[] intervalValues;

    /**
     * 执行时间
     */
    private List<String> cycleExecutionTimes;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 受理节点
     */
    private String acceptNode;

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
     * 机场编码
     */
    private String uavNestCode;

    /**
     * 无人机编码
     */
    private String uavCode;

    /**
     * 航线快照id
     */
    private String snapshotId;

    /**
     * 是否支持断点续飞
     */
    private Boolean breakPoint;

    /**
     * 镜头类型 0-自适应，1-流畅，2-标清，3-高清，4-超清，5-原画
     */
    private String definition;

    /**
     *  视频质量
     */
    private String videoQuality;

    /**
     * 复飞-原始任务id
     */
    private String originalTaskRecordIds;

    /**
     * 复飞-原始任务-图片/视频id
     */
    private String photoId;

    /**
     *  复飞-原始任务-图片/视频id
     */
    private String originalId;

    /**
     * 是否有复飞任务
     */
    private List<String> isRefly;

    /**
     * 是否是从删除任务调用处罚（0：是  其他：不是）
     */
    private String taskDeleteFlag;

    /**
     * 是否开启自动断点续飞
     */
    private Boolean autoResume;

    /**
     * 起始机巢编号
     */
    private String startNestCode	;
    /**
     * 降落机巢编号
     */
    private String endNestCode	;
    /**
     * 无人机型号编号
     */
    private String uavModelCode	;

    /**
     * 是否蛙跳：0-否，1-是
     */
    private Integer frogLeap;

    /**
     * 风险短信推送开关 ,1:开，0:关
     */
    private Integer riskMsgSwitch;

    /**
     * 安全员列表
     */
    private List<AirPlanSafetyOfficerReq> safetyOfficers;

}
