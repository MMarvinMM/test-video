package com.chinatower.airline.dto.response;

import lombok.Data;

import java.util.List;

/**
 * @author csj
 * @description
 * @date 2024/11/4 19:58
 */
@Data
public class AirDeviceInfoDetailRep {

    /**
     * 基站基础信息
     * */
    private AirDeviceInfoBaseRep baseInfo;
    /**
     * 基站信息
     * */
    private AirDeviceInfoNestRep nestInfo;
    /**
     * 无人机列表
     * */
    private List<AirDeviceInfoUavRep> uavInfoList;
}
