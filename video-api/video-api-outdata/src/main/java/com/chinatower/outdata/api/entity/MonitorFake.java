package com.chinatower.outdata.api.entity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MonitorFake implements Serializable {
    private String siteCode;
    private String deviceCode;
    private String status;
    /**
     * 纬度
     * */
    private String lat;
    /**
     * 经度
     * */
    private String lon;

    private String deviceName;

    private String provinceId;

    private String provinceName;

    private String cityId;

    private String cityName;

    private String countyId;

    private String countyName;

    private String location;

    private String modelCode;

    private String deviceModelCode;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 组织信息列表
     */
    private List<MonitorFakeDeptSubVO> deptInfoList;

    /**
     * 是否收藏 0-为收藏 1-已收藏
     */
    private Integer isCollect;
}
