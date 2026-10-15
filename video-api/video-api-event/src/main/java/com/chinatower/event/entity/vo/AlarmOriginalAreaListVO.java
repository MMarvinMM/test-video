package com.chinatower.event.entity.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;


@Data
@ToString
public class AlarmOriginalAreaListVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String taskId;
    private String importanceLevel;
    private List<AlarmOriginalAreaVO> alarmArea;

    /**
     * 告警点列表
     */
    private List<AlarmOriginalPointVO> alarmPointList;
}
