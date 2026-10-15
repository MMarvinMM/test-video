package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AlarmReasonSaveSubDTO implements Serializable {

    /**
     * 告警类型id
     */
    private Long id;

    /**
     * 告警类型原因
     */
    private String reason;

    /**
     * 告警类型原因操作类型 1-新增 2-修改 3-删除
     */
    private Integer type;
}
