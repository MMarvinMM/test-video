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
public class SearchlightSwitchDto {

    /**
     * 设备id
     */
    private String deviceCode;
    /**
     * 挂载类型
     */
    private String mountType;

    /**
     * 操作类型 0：关；1：开
     */
    private String optType;

    /**
     * 接入节点
     */
    private String accessNode;
}
