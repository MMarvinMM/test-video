package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 无人机挂载型号信息VO
 */
@Data
public class UavMountModelInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 挂载型号编码
     */
    private String modelCode;

    /**
     * 挂载型号名称
     */
    private String modelName;

    /**
     * 厂家编码
     */
    private String manufacturerCode;

    /**
     * 挂载类型
     */
    private String mountType;

    /**
     * 支持功能
     */
    private String supportFunction;
}

