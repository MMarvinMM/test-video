package com.chinatower.event.entity.dto;

import lombok.Data;

/**
 * @Author: songxinfeng
 * @Date: 2025/4/18 11:29
 */

@Data
public class TransparentFieldsDto {

    /**
     * 算法仓联动算法
     */
    private String linkageAlgorithmCode;

    /**
     * 设备厂家
     */
    private String modelCode;

    /**
     * 通道属性
     */
    private String attribute;

    /**
     * 原图
     */
    private String fileImgUrlSrc;

    /**
     * 双通道抓图
     */
    private String assistPicUrl;

    /**
     * 摄像机类型
     */
    private String deviceType;
    /**
     * 摄像机类型名称
     */
    private String deviceTypeName;

    /**
     * 摄像机子类型
     */
    private String deviceSubType;
    /**
     * 摄像机子类型名称
     */
    private String deviceSubTypeName;
}
