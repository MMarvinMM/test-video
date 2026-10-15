package com.chinatower.device.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class DeviceCameraModelVO {

    /**
     * 运管id
     */
    private Long ygId;

    /**
     * 型号编码
     */
    private String modelCode;

    /**
     * 型号名称
     */
    private String modelName;

    /**
     * 云台俯仰角范围
     */
    private String rangeOfPan;

    /**
     * 云台偏航角范围
     */
    private String rangeOfYaw;

    /**
     * 厂家编码
     */
    private String manufacturerCode;

    /**
     * 负载枚举值
     */
    private String domain;

    /**
     * 类型
     */
    private String type;

    /**
     * 父类型
     */
    private String subType;

    /**
     * 厂家名称
     */
    private String manufacturerName;

    /**
     * 支持功能
     */
    private String supportFunction;

    /**
     * 相机参数
     */
    private List<DeviceCameraModelParamVO> cameraLensParamList;
}
