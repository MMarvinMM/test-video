package com.chinatower.gis.api.entity.map;

import lombok.Data;

/**
 * @desription: 推荐行政区名称
 * @data 创建时间：2023/8/19 16:57
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class StatisticsPriorityCitys {
    /**
     * 行政区名称
     */
    private String name;
    /**
     * 城市数量
     */
    private Integer count;
    /**
     * 行政区中心点经纬度
     */
    private String lonlat;
    /**
     * 英文行政名称
     */
    private String ename;
    /**
     * 城市国标码
     */
    private String adminCode;
    /**
     * 有无下一级行政区
     */
    private String isleaf;
}
