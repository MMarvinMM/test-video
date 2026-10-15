package com.chinatower.video.api.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class VideoTimeSectionVO {

    //录像计划id
    private String recordPlanId;

    private int[] recordPlanIds;

    //日期，取值1-7(对应周一周日）
    private int dayOfWeek;

    //录像开始时间，格式：HHmmss，范围[000000~235959]
    private String startTime;

    //录像结束时间，格式：HHmmss，范围[000000~235959]
    private String endTime;

    private String deleteFlag;

    private String industryCode;


}
