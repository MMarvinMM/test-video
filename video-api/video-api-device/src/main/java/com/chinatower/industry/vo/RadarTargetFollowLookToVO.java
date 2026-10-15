package com.chinatower.industry.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RadarTargetFollowLookToVO implements Serializable {

    /**
     * 【必传】设备编码列表
     */
    private List<String> deviceCodeList;

    /**
     * 【必传】目标编码
     */
    private String targetId;

    /**
     * 【必传】同时随动状态 start:开始同时随动 stop:取消同时随动
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
