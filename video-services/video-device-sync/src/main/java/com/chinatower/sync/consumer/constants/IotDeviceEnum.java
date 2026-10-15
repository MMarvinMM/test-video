package com.chinatower.sync.consumer.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 物联设备配置枚举
 */
public interface IotDeviceEnum {

    /*
     * 厂家类型
     */
    @Getter
    @AllArgsConstructor
    enum IotDeviceTypeZoneEnum {
        TYPE_1("40084401", "ads-b设备", "400000"),
        TYPE_2("40084402", "remoteId设备", "4000"),
        TYPE_3("40084403", "频谱设备", "2000");

        private final String deviceType;
        private final String deviceName;
        private final String wZone;

        public static IotDeviceTypeZoneEnum getByDeviceType(String deviceType){
            for (IotDeviceTypeZoneEnum enums : IotDeviceTypeZoneEnum.values()) {
                if (enums.getDeviceType().equals(deviceType)){
                    return enums;
                }
            }
            return null;
        }
    }
}
