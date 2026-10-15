package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data

public class ReceiveIotAlarmBooleanDto implements Serializable {

    /**
     * 是否符合告警生效物联设备组合
     */
    private boolean isIotDeviceGroup;

    /**
     * 是否符合告警防抖规则
     */
    private boolean isAntiShake;


    /**
     * 是否符合物联规则下的阈值范围
     */
    private boolean isTrueGroup;
}
