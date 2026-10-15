package com.chinatower.device.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 喊话相关接口入参实体类
 */
@Data
public class HornRangeDevInfoDTO implements Serializable {

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 顺序
     */
    private Integer order;
}
