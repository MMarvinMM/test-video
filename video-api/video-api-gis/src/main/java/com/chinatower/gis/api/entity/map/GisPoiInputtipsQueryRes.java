package com.chinatower.gis.api.entity.map;

import lombok.Data;
/**
 * @desription: 输入提示查询返回结果
 * @data 创建时间：2026/7/15 17:19
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GisPoiInputtipsQueryRes {
    /**
     * id
     */
    private String id;

    /**
     * 店铺名称
     */
    private String name;

    /**
     * 所在区县
     */
    private String district;

    /**
     * 区域编码
     */
    private String adcode;

    /**
     * 经纬度，格式：经度,纬度
     */
    private String location;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 类型编码
     */
    private String typecode;

    /**
     * 城市信息数组
     */
    private String city;
}
