package com.chinatower.industry.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RadarTargetFollowRelayVO implements Serializable {

    /**
     * 【开始接力必传】设备编码列表
     */
    private List<String> deviceCodeList;

    /**
     * 【开始接力必传】目标编码
     */
    private String targetId;

    /**
     * 【取消接力必传】消息id
     */
    private String messageId;

    /**
     * 【必传】接力状态 start:开始接力 stop:取消接力
     */
    private String type;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 受理节点
     */
    private String acceptNode;
}
