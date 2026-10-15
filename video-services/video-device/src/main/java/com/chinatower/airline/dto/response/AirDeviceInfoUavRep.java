package com.chinatower.airline.dto.response;

import lombok.Data;

/**
 * @author csj
 * @description
 * @date 2024/11/4 19:58
 */
@Data
public class AirDeviceInfoUavRep {

    /**
     * 无人机ID
     * */
    private String uavId;
    /**
     * 无人机类型
     * */
    private String type;
    /**
     * 无人机类型名称
     * */
    private String typeName;

    /**
     * 相机名称
     * */
    private String cameraName;

    /**
     * 相机ID
     * */
    private String cameraId;

    /**
     * 无人机序列号
     * */
    private String uavNumber;

    /**
     * 遥控器序列号
     * */
    private String rcNumber;
}
