package com.chinatower.bdm.modules.inspectionRoute.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class InspectionRelBriefInfoVO implements Serializable {

    /**
     * 摄像机编号
     */
    private String deviceCode;

    /**
     * 通道编号
     */
    private String channelCode;

    /**
     * 预置位ID
     */
    private String presetId;

    /**
     * 暂停巡航标识 1-是 0-否
     */
    private Integer pauseCruiseFlag;

}
