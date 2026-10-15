package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

/**
 * 任务巡查列表入参
 * @author zl
 */
@Data
public class InspectionTaskParam extends BaseDataScopeEntity {
    /**
     * 当前页
     */
    private int pageNum;
    /**
     * 条数限制
     */
    private int pageSize;

    private String gridId;
    private String keeperId;
    private String taskStatus;
    private String confId;
    private String keeperName;
    private String startDate;
    private String endDate;
    private String createStartTime;
    private String createEndTime;
    private String taskName;
    private Integer sendFlag;

    private String flag;//页面标识 1为待办 2为已办 3为全部

    /**
     * 创建日期（对应APP任务详情的巡查日期）
     */
    private String createDate;

    /**
     * 巡查时段开始时间
     */
    private String inspectionStartTime;

    /**
     * 巡查时段结束时间
     */
    private String inspectionEndTime;

    /**
     * 反馈人网格员id
     */
    private String feedbackKeeperId;

    /**
     * 反馈人网格员名称
     */
    private String feedbackKeeperName;

    /**
     * 反馈端（1.pc 2.app）
     */
    private String feedbackEnd;

    /**
     * 反馈端名称
     */
    private String feedbackEndName;

    private String sort;

    private String sortName;

    private String taskDistributionStartTime;

    private String taskDistributionEndTime;

}
