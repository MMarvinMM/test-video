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
public class PlayTtsSetDto {

    /**
     * 设备id
     */
    private String deviceCode;
    /**
     * 挂载类型
     */
    private String mountType;

    /**
     * single:单次播放
     * cycle:循环播放
     */
    private String playType;

    /**
     * TTS播放文本
     */
    private String text;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * md5
     */
    private String md5;
}
