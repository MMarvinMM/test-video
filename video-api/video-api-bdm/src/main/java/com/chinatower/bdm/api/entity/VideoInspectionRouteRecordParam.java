package com.chinatower.bdm.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class VideoInspectionRouteRecordParam implements Serializable {

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

}
