package com.chinatower.sync.consumer.item;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 4.44.精确设备查询 请求实体类
 */
@Data
public class DeviceReadReq implements Serializable {

    /**
     * 设备编码集合
     */
    private List<String> deviceCodeList;

    /**
     * 业务类型
     * 1 备电
     * 2 换电
     * 5 视联类
     * 7 光伏
     * 0 发电
     * 4 低速充电
     * 6 车电
     * 8 汽车充电
     * 9 边缘网关
     */
    private Integer businessType = 5;

    /**
     * 平台编码
     */
    private String platformCode;

    public DeviceReadReq(List<String> deviceCodeList, String platformCode) {
        this.deviceCodeList = deviceCodeList;
        this.platformCode = platformCode;
    }

}