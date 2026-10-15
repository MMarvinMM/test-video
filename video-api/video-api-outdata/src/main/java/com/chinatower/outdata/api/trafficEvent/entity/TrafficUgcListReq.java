package com.chinatower.outdata.api.trafficEvent.entity;


import lombok.Data;

import java.io.Serializable;

/**
 * 1.1.2.1 获取指定辖区的UGC用户上报事件列表
 */
@Data
public class TrafficUgcListReq implements Serializable {

    /**
     * 辖区ID
     * @required true
     */
    private Integer nodeId;

    /**
     * 异常或常规拥堵ID
     * @required true
     * 用户上报事件类型,101拥堵、102交通事故、105故障车、405积水、401大雾、403结冰、404积雪、110施工、103交通管制、107危险，多个用逗号分隔
     */
    private String eventType;

    /**
     * 交通事件ak，非必填，不填的时候以nacos配置为准
     */
    private String trafficEventAk;
}
