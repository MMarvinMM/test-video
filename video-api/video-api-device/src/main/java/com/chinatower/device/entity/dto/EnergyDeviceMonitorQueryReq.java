package com.chinatower.device.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class EnergyDeviceMonitorQueryReq {

    private Integer pageNum;
    private Integer pageSize;
    private String startTime;
    private String endTime;
    private String devCode;
    /**
     * 业务类型 4-低速充电、8-汽车充电
     */
    private String businessType;
}
