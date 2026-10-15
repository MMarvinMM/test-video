package com.chinatower.gis.api.entity.map;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @desription: 天地图逆地理编码地址信息
 * @data 创建时间：2023/8/16 17:09
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class AddressComponent {
    /**
     * 地址
     */
    private String address;
    /**
     * 街道
     */
    private String town;
    /**
     * 街道区域编码
     */
    @JsonProperty("town_code")
    private String townCode;
    /**
     * 区县
     */
    private String county;

    /**
     * 区县区域code
     */
    @JsonProperty("county_code")
    private String countyCode;
    /**
     * 国家
     */
    private String nation;
    /**
     * 国家
     */
    @JsonProperty("poi_position")
    private String poiPosition;

    /**
     * 城市名称
     */
    private String city;
    /**
     * 城市区域编码
     */
    @JsonProperty("city_code")
    private String cityCode;
    /**
     * 此点在最近地点信息方向
     */
    @JsonProperty("address_position")
    private String addressPosition;
    /**
     * 距离此点最近poi点
     */
    private String poi;
    /**
     * 省区域编码
     */
    @JsonProperty("province_code")
    private String provinceCode;
    /**
     * 省名称
     */
    private String province;
    /**
     * 距离此点最近的路
     */
    private String road;
    /**
     *此点距离此路的距离
     */
    @JsonProperty("road_distance")
    private int roadDistance;
    /**
     *距离此点最近poi点的距离
     */
    @JsonProperty("poi_distance")
    private int poiDistance;
    /**
     *此点距离最近地点信息距离
     */
    @JsonProperty("address_distance")
    private int addressDistance;
}
