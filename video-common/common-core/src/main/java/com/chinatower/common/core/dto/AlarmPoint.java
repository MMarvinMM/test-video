package com.chinatower.common.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 告警点数据结构
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmPoint {
    private int alarmPointId;
    private double percentX;  // 百分比坐标 0.0-1.0
    private double percentY;  // 百分比坐标 0.0-1.0

}