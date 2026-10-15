package com.chinatower.platform.modules.video.entity;


import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

/**
 * @ClassName: TimeSectionVO
 * @Description: 时间选择实体
 * @Author: chenmf
 * @Date: 2022-01-24 11:51
 **/
@Data
@ToString
public class TimeSectionsVO {

    //录像计划id
    private Long recordPlanId;

    private int[] recordPlanIds;

    //日期，取值1-7(对应周一周日）
    @Range(min=1, max=7, message = "日期值有误")
    private int dayOfWeek;

    //录像开始时间，格式：HHmmss，范围[000000~235959]
    private String startTime;

    //录像结束时间，格式：HHmmss，范围[000000~235959]
    private String endTime;

    //删除标识
    private String deleteFlag;




}
