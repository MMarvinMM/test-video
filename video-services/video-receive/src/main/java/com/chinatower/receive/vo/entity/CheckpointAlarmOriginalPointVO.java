package com.chinatower.receive.vo.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class CheckpointAlarmOriginalPointVO implements Serializable {

    private static final long serialVersionUID = -127394165569187957L;
    private String warningOrderId;
    private String alarmPointId;
    private String alarmAreaId;
    private String percentX;
    private String percentY;
}

