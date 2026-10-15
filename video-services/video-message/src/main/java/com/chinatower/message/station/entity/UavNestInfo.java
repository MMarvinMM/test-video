package com.chinatower.message.station.entity;


import com.chinatower.device.entity.dto.WebSocketMsgType;
import lombok.Data;

import java.util.List;

@Data
public class UavNestInfo {
    // 时间戳
    private String timestamp;

    // 设备编码
    private String deviceCode;

    /**
     * 无人机设备编码
     */
    private String uavDeviceCode;

    // 遥测数据
    private TelemetryData data;

    /**
     * 遥测数据内部类
     */
    @Data
    public static class TelemetryData {

        private String deviceCode;

        private String timestamp;

        // 机巢概括状态（mqtt状态）
        private int state;

        // 机巢维保状态
        private int maintenanceState;

        // 机巢是否调试
        private int nestDebug;

        // 机巢是否连接
        private int nestConnected;

        // 遥控器连接状态
        private Integer rcConnected;

        // 无人机连接状态
        private Integer aircraftConnected;

        // 无人机sd卡已用空间(GB，两位小数)
        private Float airSdMemoryUseRate;

        // 网络速率(kbps，两位小数)
        private Float networkrate;

        // 待同步文件数量
        private Integer mediaFileDetail;

        // 充电状态(0-充电中；1-充电完成)
        private Boolean droneChargeState;

        // 电量(百分比)
        private Integer capacityPercent;

        // 舱内温度(摄氏度，可能多个区)
        private List<Float> temperature;

        // 空调状态(0-关机；1-开机；2-未知，可能多个区)
        private List<String> airState;

        // 舱内湿度(百分比)
        private Float humidity;

        // 舱门状态(-1-未知 0-关闭 1-打开 2-错误 3-重置)
        private Integer cabinState;

        // 风速(m/s)
        private Float windSpeed;

        // 是否有雨(0：无；1：有)
        private Boolean hasRain;

        // 雨量(毫米)
        private Float rain;

        // 环境温度(摄氏度)
        private Float environmentTemperature;

        // 环境湿度(百分比)
        private Integer environmentHumidity;

        // 气压(hPa)
        private Float pressure;

        //("飞机是否正在飞行 -1 -> 未知 0 -> 不飞行 1 -> 正在飞行")
        private String flying;

        private String industryCode;

        // 0.未标定、1.已标定
        private Integer calibration;

        // 高度
        private String altitude;

        // 经度
        private String longitude;

        // 纬度
        private String latitude;

        private WebSocketMsgType webSocketMsgType = WebSocketMsgType.UFCP_UAV_NEST_INFO;
    }
}
