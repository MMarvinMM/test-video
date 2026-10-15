package com.chinatower.event.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zheng.yan on 2022/2/17.
 */
@Data
public class AlarmTypeDataReq implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 告警类型编码
     */
    private String alarmTypeCode;

    /**
     * 告警类型名称
     */
    private String alarmTypeName;
    /**
     * 1-算法告警；2-设备告警
     */
    private int sourceType;
    /**
     * 1-新增；2-修改； 3-删除
     */
    private int optType;

    /**
     * 备注
     */
    private String remarks;
}
