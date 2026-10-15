package com.chinatower.emergency.dto;


import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StageInfoVO extends CommonFeignEntity {
    private String digitalStageId;
    private String digitalId;
    private Integer stageDays;
    private Integer stageId;
    private String eventCode;
    private String stageName;
    private String stageStartDate;
    private String stageStopDate;
    private String stageDetail;
    //阶段状态 -1:未设置阶段 0:未开始,1:执行中,2:已中止,3:已结束
    private Integer stageStatus;
    private Integer eventStatus;
    private List<TaskInfoVO> taskList;
}
