package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AlarmReasonSaveDTO implements Serializable {

    /**
     * 告警类型值
     */
    private String typeValue;

    /**
     * 告警原因列表
     */
    private List<AlarmReasonSaveSubDTO> alarmReasonList;
}
