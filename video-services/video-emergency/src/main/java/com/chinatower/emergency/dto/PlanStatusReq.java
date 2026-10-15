package com.chinatower.emergency.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlanStatusReq {
    //1:开始,2:中止,3:结束
    private Integer status;
    private Integer stageId;
    private Integer taskId;
}
