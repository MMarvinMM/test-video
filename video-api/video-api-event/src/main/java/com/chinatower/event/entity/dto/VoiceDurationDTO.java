package com.chinatower.event.entity.dto;

import lombok.Data;

@Data
public class VoiceDurationDTO {
    /**音频文件地址*/
    private String voiceUrl;

    /**音频文件时长*/
    private String voiceDuration;
}
