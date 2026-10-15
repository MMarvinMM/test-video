package com.chinatower.device.dto;

import lombok.Data;

@Data
public class RadarTargetFollowRelayResult {

    /**
     * 返回码 0：成功，1：失败
     */
    private String code;

    /**
     * 返回信息 成功：success；失败：服务异常描述信息
     */
    private String msg;

    /**
     * 业务数据
     */
    private RadarTargetFollowRelaySubDTO data;
}
