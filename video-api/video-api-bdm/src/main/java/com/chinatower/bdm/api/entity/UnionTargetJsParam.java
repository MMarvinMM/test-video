package com.chinatower.bdm.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UnionTargetJsParam implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 目标id
     */
    private String targetId;
    /**
     * 操作类型 :start/stop
     */
    private String type;

    /**
     * 设备编码
     */
    private String deviceCode;

    private String[] cameraId;

    List<UnionTargetJsDevice> deviceList;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 受理节点
     */
    private String acceptNode;

}
