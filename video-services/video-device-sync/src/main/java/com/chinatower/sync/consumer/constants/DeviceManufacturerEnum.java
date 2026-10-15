package com.chinatower.sync.consumer.constants;



import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 厂家配置枚举
 */
public interface DeviceManufacturerEnum {

    /*
     * 厂家类型
     */
    @Getter
    @AllArgsConstructor
    enum DeviceManufacturerType {
        TYPE_1("无人机", 1),
        TYPE_2("机巢", 2),
        TYPE_3("无人机型号", 3),
        TYPE_4("机巢型号", 4),
        TYPE_5("相机型号", 5),
        TYPE_6("挂载型号", 6);
        private final String typeName;
        private final Integer typeValue;
    }
}
