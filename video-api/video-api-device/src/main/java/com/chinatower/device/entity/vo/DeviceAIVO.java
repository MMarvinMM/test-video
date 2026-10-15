package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.util.List;


@Data
public class DeviceAIVO extends BaseDataScopeEntity {
    /**
     *  是否查询全国范围 1：是
     */
    private String allArea;

    /**
     *  省份名称
     */
    private String provinceName;

    /**
     *  地市名称
     */
    private String cityName;
    /**
     *  区县名称
     */
    private String countyName;

    /**
     *  省份编码
     */
    private String provinceId;

    /**
     *  地市编码
     */
    private String cityId;
    /**
     *  区县编码
     */
    private String countyId;

    /**
     *  行政区划编码
     */
    private String adCode;

    /**
     *  地址
     */
    private String location;

    /**
     *  范围
     */
    private Integer radius;

    /**
     *  标签
     */
    private String label;

    /**
     *  场景
     */
    private String labelType;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;


    /**
     * 场景id
     */
    List<Integer> industryList;

    /**
     * 摄像机编码
     */
    List<String> deviceCodeList;

    /**
     * 限制条数
     */
    int limitCount;

    private String devName;
}
