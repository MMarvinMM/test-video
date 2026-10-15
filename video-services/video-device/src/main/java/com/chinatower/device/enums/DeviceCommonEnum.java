package com.chinatower.device.enums;



import java.util.Objects;

/**
 * 根据设备类型获取对应的接口实现类bean name
 */
public enum DeviceCommonEnum {


    UAV_DEVICE(1 , "uavDevice2ServiceImpl"),

    RECORDER_DEVICE(2 , "recorderDevice2ServiceImpl"),

    RADAR_DEVICE(3 , "radarDevice2ServiceImpl"),

    IOT_DEVICE(4, "iotDevice2ServiceImpl"),

    HORN_DEVICE(5, "hornDevice2ServiceImpl"),

    UAV_NEST_DEVICE(6, "uavNestDevice2ServiceImpl");

    private Integer deviceType;

    /**
     * beanName
     */
    private String beanName;

    DeviceCommonEnum(int deviceType, String beanName) {
        this.deviceType = deviceType;
        this.beanName = beanName;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     *  根据查询类型返回对应的beanName
     * @param deviceType
     * @return
     */
    public static String getBeanName(int deviceType){
        for (DeviceCommonEnum deviceCommonEnum : DeviceCommonEnum.values()) {
            if (Objects.equals(deviceType, deviceCommonEnum.getDeviceType())) {
                return deviceCommonEnum.getBeanName();
            }
        }
        return null;
    }


}
