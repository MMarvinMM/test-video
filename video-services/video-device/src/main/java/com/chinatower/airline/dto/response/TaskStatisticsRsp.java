package com.chinatower.airline.dto.response;

import lombok.Data;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2025/4/15 11:21
 */
@Data
public class TaskStatisticsRsp {
    private Integer successCount; //成功执行
    private Integer failCount; //执行失败
    private Integer totalCount; //任务总数量
    private Integer runningCount; //执行中数量
    private Integer waitingCount; //待执行
    private List<AirTaskStatisticsRes> airTaskRes; //任务列表
}
