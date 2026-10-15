package com.chinatower.platform.entity.order;

import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName OrderInfoDTO
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/27 15:56
 * @Version 1.0
 **/
@ToString
@Data
@Alias("orderPackage")
public class OrderPackageVO implements Serializable {

    private String packageId;
    private String packageCode;
    private String packageName;
    private String description;
    private String storageTime;

    private List<OrderApplicationVO> appData;
    private List<OrderAlgorithmsVO> algorithmData;
    private List<OrderAlgorithmsVO> giftAlgorithmData;
    private List<OrderDevicesVO> deviceData;
    private List<OrderDevicesVO> sensorDeviceData;
    private List<OrderRadarDeviceVO> radarData;
    private List<OrderServiceRelVO> satelliteData;
    private List<OrderUavDeviceVO> uavDeviceData;
    private List<OrderUavNestDeviceVO> uavNestDeviceData;
    private List<OrderHornDeviceVO> hornDeviceData;
    private List<OrderRecorderDeviceVO> recorderDeviceData;
}
