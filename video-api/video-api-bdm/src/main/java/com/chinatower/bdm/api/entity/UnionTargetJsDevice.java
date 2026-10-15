package com.chinatower.bdm.api.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UnionTargetJsDevice implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 摄像机编码
     */
    private String deviceCode;
    /**
     * 通道编码
     */
    private String  channelCode;

}
