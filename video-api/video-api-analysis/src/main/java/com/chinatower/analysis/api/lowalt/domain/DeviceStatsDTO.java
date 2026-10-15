package com.chinatower.analysis.api.lowalt.domain;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;

/**
 * 设备统计查询DTO
 */
@Data
public class DeviceStatsDTO extends BaseIndustryEntity {

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备类型
     * ADS-B	1
     * 雷达	2
     * 摄像机	3
     * 频谱	4
     * RemoteID	5
     * 气象	6
     */
    private String deviceType;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private String startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private String endTime;

    /**
     * 受理编码
     */
    private String acceptNode;

    /**
     * 物联设备类型
     */
    private List<String> iotDeviceTypeList;

    /**
     * 物联设备编码
     */
    private List<String> iotDeviceCodeList;

    /**
     *  摄像机设备编码
     */
    private List<String> cameraDeviceCodeList;

    /**
     * 物联设备区域编码
     */
    private List<String> areaCodeList;


    /**
     * 查询订购设备类型 0全部 1物联设备 2 摄像机设备
     */
    private String orderFlag;


    /** 区域级别：1=国家级,2=省份级,3=地市级,4=区县级
     *  前端组件封装直辖市做了特殊处理，直辖市传省级编码，区域级别传 23=直辖市
     */
    private String areaLevel;

    /** 区域编码 */
    private String areaCode;

    /**
     * 省份
     */
    private String provinceId;

    /**
     * 地市
     */
    private String cityId;

    /**
     * 区县
     */
    private String countyId;
}