package com.chinatower.gis.api.entity.map;

import lombok.Data;

import java.util.List;

/**
 * @desription: 高德行政区域
 * @data 创建时间：2023/8/28 15:37
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GdDistricts {
    /**
    *城市编码
    * @required false
    */
    private String cityCode;
    /**
     * 区域编码
     * @required false
     */
    private String adCode;
    /**
     *行政区名称
     * @required false
     */
    private String name;
    /**
     *行政区边界坐标点
     * @required false
     */
    private String polyline;
    /**
     *行政区边界坐标点 离线使用
     * @required false
     */
    private String polygon;
    /**
     * 区域中心点
     * @required false
     */
    private String center; /**
     *行政区划级别
     * @required false
     */
    private String level;
    /**
     * 街道CODE
     */
    private String streetid;
    /**
     * 下级行政区列表，包含district元素
     * @required false
     */
    private List<GdDistricts> districts;

}
