package com.chinatower.device.service;

import java.util.List;

public interface WaterLevelMongoBase {
    String getPicAddr();
    void setImageList(List<String> imageList);
    List<String> getImageList();
    String getDeviceCode();
    void setDeviceName(String deviceName);
    String getDeviceName();
    void setDeviceStatus(String deviceStatus);
    String getDeviceStatus();
    void setDeviceAddress(String deviceAddress);
    String getDeviceAddress();
}
