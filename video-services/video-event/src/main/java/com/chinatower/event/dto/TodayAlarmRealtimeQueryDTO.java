package com.chinatower.event.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 今日告警实时统计查询参数（tableName 由服务端填充）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TodayAlarmRealtimeQueryDTO extends StatisticsDTO {

    /** 当月告警月表名，如 vm_warning_order_t_202608 */
    private String tableName;
}
