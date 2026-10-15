package com.chinatower.event.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PushThirdVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private PushAlarmThirdVO data;
}