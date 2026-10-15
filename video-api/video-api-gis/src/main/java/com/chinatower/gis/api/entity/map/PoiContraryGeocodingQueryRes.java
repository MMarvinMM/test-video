package com.chinatower.gis.api.entity.map;

import lombok.Data;

import java.util.List;

/**
 * @desription: 逆地理编码查询返回
 * @data 创建时间：2023/8/15 15:15
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class PoiContraryGeocodingQueryRes {
    /**
     * 地址
     * @required true
     */
    private String address;
    /**
     *经纬度
     * @required true
     */
    private String location;

    /**
     * 区域
     * @required false
     */
    private Region region;
    /**
     * 线
     * @required false
     */
    private List<Pois> pois;
}
