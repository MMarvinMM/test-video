package com.chinatower.event.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 二次识别报警区域参数
 * @author cuisj
 * @date 2024-01-11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecondRecogBackAreaVO {

    /**
     * 告警区域id
     */
    private String alarmAreaId;

    /**
     * 置信度
     */
    private float confidenceLevel;

}
