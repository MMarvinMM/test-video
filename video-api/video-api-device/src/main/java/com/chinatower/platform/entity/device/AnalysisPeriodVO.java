package com.chinatower.platform.entity.device;

import lombok.Data;

import java.util.List;

@Data
public class AnalysisPeriodVO {

    //分析时段序号
    private String periodId;

    //开始时间
    private String startTime;

    //结束时间
    private String endTime;
}
