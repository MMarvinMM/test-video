package com.chinatower.device.dto;

import com.chinatower.device.service.WaterLevelMongoBase;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author lzt
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="water_level_latest")
public class WaterLevelLatestMongo extends BaseMongoDTO implements WaterLevelMongoBase,Serializable {

    private String id;

    private String msgid;

    private String picAddr;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date gatherTime;

    private String deviceCode;

    private String waterLevel;

    private String deviceName;

    private String deviceAddress;

    private String deviceStatus;

    private List<String> imageList;

    // 实现接口中的方法
    @Override
    public String getPicAddr() { return picAddr; }
    @Override
    public void setImageList(List<String> imageList) { this.imageList = imageList; }
    @Override
    public List<String> getImageList() { return imageList; }
    @Override
    public String getDeviceCode() { return deviceCode; }
    @Override
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    @Override
    public String getDeviceName() { return deviceName; }
    @Override
    public void setDeviceStatus(String deviceStatus) { this.deviceStatus = deviceStatus; }
    @Override
    public String getDeviceStatus() { return deviceStatus; }
    @Override
    public void setDeviceAddress(String deviceAddress) { this.deviceAddress = deviceAddress; }
    @Override
    public String getDeviceAddress() { return deviceAddress; }
}
