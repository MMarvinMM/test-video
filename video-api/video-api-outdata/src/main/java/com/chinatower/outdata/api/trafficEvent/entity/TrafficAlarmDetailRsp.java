package com.chinatower.outdata.api.trafficEvent.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 1.1.1.2 获取指定拥堵预警的拥堵详情响应实体类
 */
@Data
public class TrafficAlarmDetailRsp implements Serializable {

    /**
     * 事件ID
     */
    private String eventId;

    /**
     * 是否异常
     * 1-异常 2-非异常
     */
    private String abnormal;

    /**
     * 道路名称
     */
    private String roadName;

    /**
     * 开始时间（时间戳）
     */
    private String startTime;

    /**
     * 结束时间（时间戳）
     */
    private String endTime;

    /**
     * 拥堵类型
     * 1-高疑似事件拥堵 2-异常拥堵，3-常规拥堵
     */
    private String eventSource;

    /**
     * 拥堵事件位置语义化描述
     */
    private String semantic;

    /**
     * 拥堵指数
     */
    private String congestIndex;

    /**
     * 平均速度km/h
     */
    private String speed;

    /**
     * 拥堵距离km
     */
    private String congestLength;

    /**
     * 拥堵指数
     */
    private String congestDuration;

    /**
     * 拥堵源坐标
     */
    private String congestSourceLoc;

    /**
     * 拥堵源描述
     */
    private String congestSourceDesc;

    /**
     * 拥堵末端描述
     */
    private String congestTailDesc;

    /**
     * link的拥堵状态
     * (依次对于的拥堵等级为1-4，同一段用“,”分割，多段用”;”分割，务必排序)；拥堵等级对应地图上的红黄绿，1-畅通-绿色；2-缓行-黄色；3-拥堵-红色；4-严重拥堵-深红色；
     */
    private Map<String, String>  linkStates;

    /**
     * 拥堵源区域
     */
    private String congestSourceArea;

    /**
     * 拥堵影响辖区
     */
    private String congestAffectArea;


    /**
     * 绑定的UGC事件列表，若无为null
     */
    private List<TrafficUgcListRsp> baiduEvents;

}
