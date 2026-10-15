package com.chinatower.bdm.api.entity;

import com.chinatower.common.annotation.Excel;
import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.List;

/**
 * 巡查任务列表VO
 */
@Data
@ToString
@Alias("InspectionTaskListVO1")
public class InspectionTaskListVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String taskId;
    private String taskName;
    private String taskDesc;
    @Excel(name = "巡查网格")
    private String gridName;
    @Excel(name = "巡查人")
    private String keeperName;
    private String startTime;
    private String endTime;
    @Excel(name = "巡查日期")
    private String createDate;
    @Excel(name = "巡查时段")
    private String inspectionPeriod;
    @Excel(name = "任务下发时间")
    private String createTime;
    private String retroaction;

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

    @Excel(name = "反馈人")
    private String feedbackKeeperName;


    @Excel(name = "巡查反馈时间")
    private String inspectionTime;

    /**
     * 反馈端（1.pc 2.app）
     */
    @Excel(name = "反馈端",readConverterExp = "1=PC,2=APP")
    private String feedbackEnd;

    /**
     * 反馈端名称
     */
    private String feedbackEndName;

    @Excel(name = "是否完成",readConverterExp = "1=已完成,2=未完成")
    private String taskStatus;

    /**
     * 巡查人id(多个)
     */
    private String keeperId;

    /**
     * 网格长列表
     */
    private List<String> isGridManagerList;

    /**
     * 网格员列表
     */
    private List<String> noGridManagerList;

    /**
     * 待办标识
     */
    private String toDoFlag;
}
