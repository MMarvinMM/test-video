package com.chinatower.stdu.common.enums;


import com.chinatower.common.core.utils.StringUtils;

public enum TableNameEnum {
    DEVICE("-1", "vm_order_devices"),
    UAV("11", "vm_order_uav_devices"),
    RECORDER("12", "vm_order_recorder_devices");

    private String deviceType;
    private String tableName;

    TableNameEnum(String deviceType, String tableName) {
        this.deviceType = deviceType;
        this.tableName = tableName;
    }

    public String getDeviceType() {
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
    public static String getTableName(String deviceType) {
        if (!StringUtils.isBlank(deviceType)) {
            for (TableNameEnum tableNameEnum : TableNameEnum.values()){
                if(tableNameEnum.getDeviceType().equals(deviceType)){
                    return tableNameEnum.getTableName();
                }
            }
        }
        return DEVICE.getTableName();
    }
}
