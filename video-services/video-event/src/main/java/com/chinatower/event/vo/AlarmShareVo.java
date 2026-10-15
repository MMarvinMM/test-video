package com.chinatower.event.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AlarmShareVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String shareId;
    private String shareAlarm;
}
