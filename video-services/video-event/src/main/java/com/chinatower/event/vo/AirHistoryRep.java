package com.chinatower.event.vo;

import lombok.Data;
@Data
public class AirHistoryRep{
    /**
     * 轨迹点记录时间  格式：yyyy-MM-dd HH:mm:ss
     * */
    private String saveDateTime;

    /**
     * 无人机定位信息
     * */
    private AircraftLocation aircraftLocation;

    @Data
    public class AircraftLocation{
        private double latitude;
        private double longitude;
    }

}
