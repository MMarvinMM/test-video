package com.chinatower.device.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * AIot能力平台通用请求体
 */
@Data
public class AIotPlatformCommonReq {

    /**
     * 设备编码
     */
    private String devCode;

    /**
     * 设备编码列表 和上面二传一
     */
    private List<String> devCodeList;

    /**
     * 设备附属编码列表
     */
    private List<String> subDevCode;

    /**
     * 指令类型 0:单一指令 1：复合指令(定制化指令) 默认0
     */
    private String type;

    /**
     * 能力编码
     */
    private String fnId;

    /**
     * 能力值
     */
    private Object fnValue;

    /**
     * 授理节点
     */
    private String acceptNode;

    /**
     * 接入节点
     */
    private String accessNode;

}
