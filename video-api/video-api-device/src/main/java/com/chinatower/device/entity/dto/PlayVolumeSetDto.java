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
public class PlayVolumeSetDto {

    /**
     * 设备id
     */
    private String deviceCode;
    /**
     * 挂载类型
     */
    private String mountType;

    /**
     * 设置类型 volumn:音量
     * 成至MP20特殊设置:
     * tts_speak_speed:TTS语速设置
     * tts_speak_language:TTS语音设置
     */
    private String optType;
    /**
     * volumn:0-100
     * 成至MP20特殊设置:
     * tts_speak_speed:0、1、2、3、4
     * tts_speak_language:0-35
     */
    private String value;

    /**
     * 接入节点
     */
    private String accessNode;
}
