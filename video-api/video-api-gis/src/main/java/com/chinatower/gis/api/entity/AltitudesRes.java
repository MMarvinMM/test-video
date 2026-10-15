package com.chinatower.gis.api.entity;

import lombok.Data;

/**
 * @desription: 查询海拔高度返回
 * @data 创建时间：2024/1/12 10:29
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class AltitudesRes {

    /**
     * 海拔高度
     */
    private Integer altitudesHeight;
    /**
     * 经纬度
     */
    private String lonLat;
    /**
     * 设备编码
     */
    private String deviceId;
}
