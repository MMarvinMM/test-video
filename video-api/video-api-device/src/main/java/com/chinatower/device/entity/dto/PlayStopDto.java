package com.chinatower.device.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cuibaoluo
 * @date 2025/12/15
 * @description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayStopDto {

    /**
     * 设备id
     */
    private String deviceCode;
    /**
     * 挂载类型
     */
    private String mountType;

    /**
     * 接入节点
     */
    private String accessNode;
}
