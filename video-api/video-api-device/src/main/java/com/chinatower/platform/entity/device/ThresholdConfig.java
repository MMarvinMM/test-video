package com.chinatower.platform.entity.device;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThresholdConfig implements Serializable {

    private String alarmType;

    private String triggerRule;
}
