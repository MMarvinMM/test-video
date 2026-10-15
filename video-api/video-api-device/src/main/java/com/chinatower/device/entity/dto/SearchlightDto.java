package com.chinatower.device.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cuibaoluo
 * @date 2025/12/16
 * @description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchlightDto {

    /**
     * 设备id
     */
    private String deviceCode;
    /**
     * 挂载类型
     */
    private String mountType;

    /**
     * 设置类型 brightness:亮度
     */
    private String optType;
    /**
     * volumn:0-100
     */
    private String value;

    /**
     * 接入节点
     */
    private String accessNode;
}
