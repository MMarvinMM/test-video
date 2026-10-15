package com.chinatower.video.modules.common.enums;


import com.chinatower.common.core.utils.StringUtils;

import java.util.Objects;

public enum DeviceTableNameEnum {
    DEVICE(1, "vm_device_info_t"),

    HORN(2, "vm_horn_device_info_t"),
    UAV(11, "vm_uav_device_info_t"),
    RECORDER(12, "vm_recorder_device_info_t");

    private Integer deviceType;
    private String tableName;

    DeviceTableNameEnum(Integer deviceType, String tableName) {
        this.deviceType = deviceType;
        this.tableName = tableName;
    }

    public Integer getDeviceType() {
        return this.deviceType;
    }

    public String getTableName() {
        return this.tableName;
    }

    /**
     * 根据传入的设备类型传入表名称
     * @param deviceType
     * @return
     */
    public static String getTableName(Integer deviceType) {
        if (Objects.nonNull(deviceType)) {
            for (DeviceTableNameEnum tableNameEnum : DeviceTableNameEnum.values()) {
                if(Objects.equals(tableNameEnum.getDeviceType(), deviceType)) {
                    return tableNameEnum.getTableName();
                }
            }
        }
        return DEVICE.getTableName();
    }
}
