package com.chinatower.platform.entity.devicemgmt.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TowerDeviceLabelSelectedSubVO implements Serializable {

    private Long labelId;

    private String labelName;

    /**
     * 是否关联 0-未关联 1-已关联
     */
    private Integer isRel;
}
