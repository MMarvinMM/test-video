package com.chinatower.video.modules.videorecordplan.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author LQ
 */
@Data
public class ChannelVideoPlanDTO {

    // 录像计划ID
    private Long recordPlanId;
    // 开始日期
    private String startDate;
    // 结束日期
    private String endDate;
    // 星期几（1-7，1=星期一，7=星期日）
    private Integer dayOfWeek;
}
