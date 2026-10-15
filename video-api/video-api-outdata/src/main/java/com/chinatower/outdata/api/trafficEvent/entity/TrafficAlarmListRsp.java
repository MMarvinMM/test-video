package com.chinatower.outdata.api.trafficEvent.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 1.1.1.1 获取指定辖区拥堵预警列表响应实体类
 */
@Data
public class TrafficAlarmListRsp implements Serializable {

    /**
     * 开始时间（时间戳）
     */
    private String startTime;

    /**
     * 事件ID
     */
    private String eventId;

    /**
     * 道路方向
     */
    private String direction;

    /**
     * 道路类型
     */
    private String roadType;

    /**
     * 道路名称
     */
    private String roadName;

    /**
     * 拥堵事件位置点坐标
     */
    private String location;

    /**
     * 辖区ID
     */
    private String nodeId;

    /**
     * 拥堵事件位置语义化描述
     */
    private String semantic;

    /**
     * 拥堵距离km
     */
    private String congestLength;

    /**
     * 平均速度km/h
     */
    private String speed;

    /**
     * 拥堵指数
     */
    private String congestIndex;

    /**
     * 拥堵指数
     */
    private String congestDuration;

    /**
     * 拥堵类型
     * 1-高疑似事件拥堵 2-异常拥堵，3-常规拥堵
     */
    private String eventSource;

    /**
     * 额外状态信息
     */
    private String extraEventStatus;

    /**
     * 辖区展示 名称
     */
    private String nodeName;

    /**
     * 绑定UGC事件类型，
     * -1为无绑定的UGC事件，其他值为事件类型
     * 101拥堵、102交通事故、105故障车、405积水、401大雾、403结冰、404积雪、110施工、103交通管制、107危险
     */
    private String iconType;

    /**
     * link的拥堵状态
     * (依次对于的拥堵等级为1-4，同一段用“,”分割，多段用”;”分割，务必排序)；拥堵等级对应地图上的红黄绿，1-畅通-绿色；2-缓行-黄色；3-拥堵-红色；4-严重拥堵-深红色；
     */
    private Map<String, String>  linkStates;

    /**
     * 是否异常
     * 1-异常 2-非异常
     */
    private String abnormal;

    /**
     * 结束时间（时间戳）
     */
    private String endTime;

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
     * 拥堵源区域code
     */
    private String congestSourceAreaCode;

    /**
     * 拥堵源区域
     */
    private String congestSourceArea;

    /**
     * 拥堵影响辖区code，多个辖区以”,”分隔
     */
    private String congestAffectAreaCode;

    /**
     * 拥堵影响辖区
     */
    private String congestAffectArea;

    /**
     * 绑定的UGC事件列表，若无为null
     */
    private List<TrafficUgcListRsp> baiduEvents;
}
