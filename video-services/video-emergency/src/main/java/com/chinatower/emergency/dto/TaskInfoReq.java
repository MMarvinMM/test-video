package com.chinatower.emergency.dto;


import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskInfoReq extends BaseDataScopeEntity {
    private Integer stageId;
    private String taskName;
    //任务开始时间
    private String taskStartDateBegin;
    //任务开始时间
    private String taskStartDateEnd;
    //任务结束时间
    private String taskStopDateBegin;
    //任务结束时间
    private String taskStopDateEnd;
    //任务状态 0:未开始,1:执行中,2:已中止,3:已结束
    private Integer taskStatus;
    //登录人userName
    private String userId;
    /**
     * 当前页
     */
    private Integer pageNum;
    /**
     * 条数限制
     */
    private Integer pageSize;

    /**
     * 事件编码
     */
    private String eventCode;

    private List<String> eventCodes;
}
