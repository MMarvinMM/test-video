package com.chinatower.outdata.api.trafficEvent.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 1.1.1.3 获取指定拥堵预警的拥堵指标曲线数据响应实体类
 */
@Data
public class TrafficAlarmCurvelRsp implements Serializable {

    /**
     * 数据时间（时间戳）
     */
    private String datatime;

    /**
     * 拥堵距离km
     */
    private String congestLength;

    /**
     * 平均速度km/h
     */
    private String speed;

    /**
     * 平日拥堵指数
     */
    private String cmpCongestIndex;

    /**
     * 平日平均速度
     */
    private String cmpSpeed;

    /**
     * link的拥堵状态
     * (依次对于的拥堵等级为1-4，同一段用“,”分割，多段用”;”分割，务必排序)；拥堵等级对应地图上的红黄绿，1-畅通-绿色；2-缓行-黄色；3-拥堵-红色；4-严重拥堵-深红色；
     */
    private Map<String, String>  linkStates;

}
