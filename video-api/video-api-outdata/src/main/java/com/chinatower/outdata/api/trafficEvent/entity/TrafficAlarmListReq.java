package com.chinatower.outdata.api.trafficEvent.entity;


import lombok.Data;

import java.io.Serializable;

/**
 * 1.1.1.1 获取指定辖区拥堵预警列表请求实体类
 */
@Data
public class TrafficAlarmListReq implements Serializable {

    /**
     * 辖区ID
     * @required true
     */
    private Integer nodeId;

    /**
     * 辖区ID
     * @required true
     * 辖区管辖的道路类型
     * nodeId类型为省高速及辖区时，使用101-省高速 102-国道 103-省道 ，为0时默认101,120,103
     * nodeId类型为市及辖区时，使用1-高速 2-环城及快速路 3-主干 4-次干 5-支干，为0时默认1,2,3,4,5
     */
    private String roadType;

    /**
     * 拥堵类型
     * @required true
     * 1-高疑似拥堵 2-异常拥堵，3-常规拥堵
     */
    private String eventSource;

    /**
     * 最小指数,默认0
     */
    private Float minIndex;

    /**
     * 最小里程(km),默认0
     */
    private Float minMileage;

    /**
     * 最小拥堵时长(min),默认0
     */
    private Float minDuration;

    /**
     * 返回结果的坐标类型
     * bd09ll：百度经纬度坐标
     * gcj02：国测局02坐标
     * 默认bd09ll
     */
    private Float retCoordType;

    /**
     * 返回结果类型参数
     * 若不传或传1，返回值无变化
     * 若传2，返回值每个事件分别新增了事件详情信息
     */
    private Integer returnType;

    /**
     * 交通事件ak，非必填，不填的时候以nacos配置为准
     */
    private String trafficEventAk;
}
