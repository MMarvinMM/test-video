package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class QueryRecordTimeData {
    private int dayOfWeek;
    private String startTime;
    private String endTime;
}
