package com.chinatower.bdm.api.entity;

import com.chinatower.common.annotation.Excel;
import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

/**
 * 巡查任务列表VO
 */
@Data
@ToString
@Alias("InspectionTaskListVOExtend1")
public class InspectionTaskListVOExtend implements Serializable {
    private static final long serialVersionUID = 1L;

    private String confId;
    private String taskId;
    private String taskName;
    private String taskDesc;
    private String gridId;
    @Excel(name = "巡查网格")
    private String gridName;
    private String keeperId;
    @Excel(name = "巡查人")
    private String keeperName;
    private String startTime;
    private String endTime;
    @Excel(name = "巡查任务时间")
    private String createTime;
    @Excel(name = "任务反馈时间")
    private String inspectionTime;
    @Excel(name = "是否完成",readConverterExp = "0=待认领,1=已完成,2=未完成")
    private String taskStatus;
    private String tenantId;
    private String retroaction;
    private String fjId;
    private String voiceUrl;
    private String videoUrl;
    private String sendFlag;
    private String industryCode;
    private String appVerCode;

}
