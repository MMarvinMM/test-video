package com.chinatower.platform.modules.keeper.entity;

import lombok.Data;

/**
 * 巡查任务
 */
@Data
public class InspectionTaskVO {
    //配置id
    private String confId;
    //巡查任务名称
    private String cnofName;
    //租户id
    private String tenantId;
    //网格id
    private String gridId;
    //网格名称
    private String gridName;
    //完成状态 0-停用；1-已完成；2-未完成
    private String taskStatus;
    //任务开始时间
    private String startTime;
    //任务结束时间
    private String endTime;
    //巡查人
    private String keeperId;
    //网格员名称
    private String keeperName;
    //配置描述
    private String confDesc;
    //行业编码
    private String industryCode;
    //行业版本编码
    private String appVerCode;
    //创建时间
    private String createTime;
    //巡查任务ID
    private String taskId;
    //巡查任务名称
    private String taskName;
    //用户ID
    private String UserId;
    //下发状态 0-未下发 1-已下发
    private Integer sendFlag;
    //删除状态: 0正常，删除1
    private Integer deleteFlag;
    //巡查时段
    private String inspectionPeriod;
    /**
     * 是否重复 1-重复；0-不重复
     */
    private String isRepeat;

    //巡查时段开始时间
    private String inspectionStartTime;
    //巡查时段结束时间
    private String inspectionEndTime;

    //APP消息列表跳转URL
    private String appUrl;
}
