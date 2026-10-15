package com.chinatower.airline.dto.response;

import lombok.Data;

import java.util.List;

/**
 * @author csj
 * @description
 * @date 2024/11/4 19:58
 */
@Data
public class AirDeviceInfoUavConfigRep {

    private String id;
    /**
     * 飞行器型号配置ID
     * */
    private String uavConfigId;
    /**
     * 飞行器型号
     * */
    private String uavType;
    /**
     * 生产厂家
     * */
    private String uavFactory;
    /**
     * 大疆负载枚举domain
     * */
    private String djiDomain;

    /**
     * 大疆负载枚举type
     * */
    private String djiType;

    /**
     * 大疆负载枚举sub_type
     * */
    private String djiSubType;

    /**
     * 飞行器类别
     * */
    private String airCategory;
    /**
     * 飞行器类型
     * */
    private String airType;
    /**
     * 起飞全重     单位：公斤(kg)
     * */
    private Double weight;
    /**
     * 支持相机
     * */
    private List<String> cameras;

    /**
     * 最大飞行时间  单位：分钟(min)
     * */
    private Double enduranceTime;


    /**
     * 最大水平速度
     * */
    private Integer maxHorizontalSpeed;

}
