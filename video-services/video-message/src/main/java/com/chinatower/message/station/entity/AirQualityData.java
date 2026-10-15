package com.chinatower.message.station.entity;

import com.alibaba.fastjson.JSON;
import com.chinatower.device.entity.dto.WebSocketMsgType;
import com.google.gson.JsonObject;
import lombok.Data;
import springfox.documentation.spring.web.json.Json;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author WLF
 * @description
 * @date 2025/5/21 17:02
 */
@Data
public class AirQualityData{
    private LingxiuData lingxiu;
    private String taskRecordId;
    private String flightRouteId;
    private String uavDeviceCode;
    private String nestDeviceCode;
    private String industryCode;
    private String createTime;
    private WebSocketMsgType webSocketMsgType = WebSocketMsgType.UFCP_UAV_AIRDATA;
    @Data
    public static class LingxiuData{
        private Map<String, Object> airData;
        private String airDatas;
        private double altitude;
        private double hdop;
        private String humidity;
        private double latitude;
        private double longitude;
        private double pressure;
        private String sateNum;
        private String sequence;
        private String serial;
        private double temperature;
        private String utcTime;
        public void setAirDataAsJson(Map<String, Object> airData) {
            this.airDatas = JSON.toJSONString(airData);
        }
    }
}
