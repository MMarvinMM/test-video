package com.chinatower.gis.api.entity.map;

import lombok.Data;

/**
 * @desription: 轨迹路由
 * @data 创建时间：2023/8/15 15:44
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class Routs {


    /**
     * 路线距离，单位米
     * @required true
     */
    private String distance;
    /**
     * 坐标点序列，格式为坐标串，如：116.481247,39.990704;116.481270,39.990726
     * @required true
     */
    private String polyline;
    /**
     * 分段道路名
     * @required true
     */
    private String roadname;
    /**
     * 路线主要动作
     * @required false
     */
    private String action;
    /**
     * 时长 时长 单位：秒
     */
    private String duration;
}
