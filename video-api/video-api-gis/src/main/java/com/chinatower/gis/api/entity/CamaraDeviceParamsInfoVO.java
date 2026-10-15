package com.chinatower.gis.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author lisy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CamaraDeviceParamsInfoVO {

    /**
     * 设备经度
     */
    private Double longitude;

    /**
     * 设备纬度
     */
    private Double latitude;

    /**
     * 设备编码（一键告警额外需要传设备编码）
     * @required true
     */
    private String deviceCode;

    /**
     * 设备挂高
     */
    private Double height;

    /**
     * 事发地点经度
     */
    private Double locationLongitude;

    /**
     * 事发地点纬度
     */
    private Double locationLatitude;

    /**
     * 查询列表页面大小
     */
    private Integer pageSize = 100;

    /**
     * 事发半径
     */
    private Double radius = 10000.0d;

}
