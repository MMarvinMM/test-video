package com.chinatower.platform.dto;

import lombok.Data;

import java.util.List;

@Data
public class SatelliteAlarmResp {

    /**
     * 返回列表数据
     */
    private List<ReceiveSatelliteAlarmDto> list;
    /**
     * 返回总数
     */
    private long total;
}
