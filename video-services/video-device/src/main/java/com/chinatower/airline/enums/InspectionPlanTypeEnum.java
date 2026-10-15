package com.chinatower.airline.enums;

import lombok.Getter;

/**
 * @author WLF
 * @description
 * @date 2024/12/12 11:10
 */
@Getter
public enum InspectionPlanTypeEnum {
    /**
     * 0：定期
     */
    REGULAR("定期"),
    /**
     * 1：周期
     */
    CYCLE("周期"),
    /**
     * 2：立即
     */
    SOON("立即"),
    /**
     * 3：持续
     */
    DURATIVE("持续"),
    ;

    /**
     * 中文描述
     */
    private final String cnName;

    InspectionPlanTypeEnum(String cnName) {
        this.cnName = cnName;
    }
}
