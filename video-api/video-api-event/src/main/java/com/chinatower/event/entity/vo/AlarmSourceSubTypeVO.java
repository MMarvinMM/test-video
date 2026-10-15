package com.chinatower.event.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 告警来源及来源对应的告警类型
 */
@Data
public class AlarmSourceSubTypeVO implements Serializable {
    /**
     * 告警来源
     */
    private String alarmSource;

    /**
     * 告警来源名称
     */
    private String alarmSourceName;

    /**
     * 告警类型信息列表
     */
    private List<AlarmTypeVO> alarmTypeVOList;

}
