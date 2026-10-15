package com.chinatower.outdata.api.trafficEvent.entity;


import lombok.Data;

import java.io.Serializable;

/**
 * 1.1.1.2 获取指定拥堵预警的拥堵详情请求实体类
 * 1.1.1.3 获取指定拥堵预警的拥堵指标曲线数据请求实体类
 */
@Data
public class TrafficAlarmDetailReq implements Serializable {

    /**
     * 辖区ID
     * @required true
     */
    private Integer nodeId;

    /**
     * 异常或常规拥堵ID
     * @required true
     */
    private String eventId;

    /**
     * 交通事件ak，非必填，不填的时候以nacos配置为准
     */
    private String trafficEventAk;
}
