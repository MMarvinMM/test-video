package com.chinatower.gis.api.entity.map;

import lombok.Data;

/**
 * @desription:
 * @data 创建时间：2023/9/7 9:09
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class District {
    /**
     * 名称
     */
    private String name;
    /**
     * 经纬度
     */
    private String center;
    /**
     * 多边形
     *  @required false
     */
    private String polygon;
    /**
     * 行政编码
     */
    private String adCode;
    /**
     * 搜索的级别
     */
    private String level;
}
