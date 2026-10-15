package com.chinatower.airline.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author csj
 * @description
 * @date 2024/11/4 19:58
 */
@Data
public class AirDeviceInfoRep {

    /**
     * 基站信息
     * */
    private AirDeviceInfoDetailRep nestDetail;
    /**
     * 左云台信息
     * */
    private AirDeviceInfoCameraRep leftCameraConfig;
    /**
     * 右云台信息
     * */
    private AirDeviceInfoCameraRep rightCameraConfig;
    /**
     * 基站相机字典
     * */
    private List<AirDeviceInfoCameraTypeRep> nestCameraTypeDict;
    /**
     * 无人机类型配置
     * */
    private Map<String,AirDeviceInfoUavConfigRep> uavTypeConfig;


    /**
     * 无人机类型字典
     * */
    private List<AirDeviceInfoCameraTypeRep> uavTypeDict;

    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 设备名称，有可能是无人机，也有可能是机巢
     */
    private String deviceName;

}
