package com.chinatower.gis.api.entity.map;

import lombok.Data;

/**
 * @desription: 天地图行政区域返回实体
 * @data 创建时间：2023/8/16 9:08
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class MapWorldAreaData {
    /**
     * 经度
     */
    private String lnt;
    /**
     * 城市类型
     */
    private String adminType;
    /**
     * 行政区划英文简称。
     */
    private String englishabbrevation;
    /**
     * 行政区划简称
     */
    private String nameabbrevation;
    /**
     * 行政区划等级。
     */
    private String level;
    /**
     * 行政区划码。
     */
    private String cityCode;
    /**
     * 四角点坐标
     */
    private String bound;
    /**
     * 行政区划名称。
     */
    private String name;
    /**
     * 行政区划英文名称。
     */
    private String english;
    /**
     * 纬度
     */
    private String lat;
    /**
     * 上级数据
     */
    private Parents parents;
}
