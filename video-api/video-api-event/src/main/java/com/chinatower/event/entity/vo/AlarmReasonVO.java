package com.chinatower.event.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AlarmReasonVO implements Serializable {

    /**
     *  告警类型ID
     */
    private Long id;

    /**
     * 类型名称
     */
    private String reason;
}
