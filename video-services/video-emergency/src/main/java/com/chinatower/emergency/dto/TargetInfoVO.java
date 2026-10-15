package com.chinatower.emergency.dto;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TargetInfoVO extends CommonFeignEntity {
    private Integer id;
    private String targetId;
    private String digitalId;
    private Integer targetDays;
    private Integer stageNum;
    private String eventCode;
    private String globalStartDate;
    private String globalStopDate;
    private String globalDetail;
    private List<Integer> stageIdList;
}
