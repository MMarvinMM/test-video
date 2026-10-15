package com.chinatower.emergency.dto;


import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeedbackInfoVO extends CommonFeignEntity {
    private Integer id;
    private Integer taskId;
    private Integer taskProgress;
    private String feedbackDetail;
    private Integer stageStatus;
    //处理端，0:pc 1:app
    private String dealClient;
    //反馈时间
    private String feedbackTime;
    //反馈人姓名
    private String feedbackName;
    //反馈人手机号
    private String feedbackPhone;
    private List<Integer> taskIdList;
    private List<EmergencyAttachVO> imgUrlList;
    private List<EmergencyAttachVO> videoUrlList;
    private List<EmergencyAttachVO> fileUrlList;
}
