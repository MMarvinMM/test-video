package com.chinatower.device.entity.dto;

import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;

import java.util.List;

@Data
public class StatisticalAnalysisReq extends PageDTO {

    /**
     * 组织id
     */
    private String orgId;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 无人机编码集合
     */
    private List<String> uavCodes;


    /**
     * 飞行趋势查询类型 0-按周 2-按月 3-按年
     */
    private String flyTrendQueryType;

    /**
     * 起飞时刻统计查询类型 0-按天 2-按周 3-按月
     */
    private String flyTimeStatisticalQueryType;
}
