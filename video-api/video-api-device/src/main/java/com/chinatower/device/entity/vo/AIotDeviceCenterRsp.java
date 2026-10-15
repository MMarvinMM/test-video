package com.chinatower.device.entity.vo;

import lombok.Data;

/**
 * AIot设备中心通用返回体
 */
@Data
public class AIotDeviceCenterRsp {

    /**
     * 状态码
     */
    private String code;

    /**
     * 消息
     */
    private String msg;

    /**
     * 返回数据集合
     */
    private Object data;

}
