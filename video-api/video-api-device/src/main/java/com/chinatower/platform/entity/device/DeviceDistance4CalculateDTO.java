package com.chinatower.platform.entity.device;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DeviceDistance4CalculateDTO implements Serializable {

    /**
     * 经度
     */
    private String longitude;
    /**
     * 纬度
     */
    private String latitude;
    /**
     * 检索半径，单位：米
     */
    private String radius;

    /**
     * 设备类型列表
     */
    private List<String> deviceTypeList;

    /**
     * 开始日期 yyyy-MM-dd HH:mm:ss 格式
     */
    private String startDate;

    /**
     * 结束日期 yyyy-MM-dd HH:mm:ss 格式
     */
    private String endDate;
}
