package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 加锁响应实体类
 * @author lc
 */
@Data
public class HornAuditionResultVO implements Serializable {

    /**
     * 文本内容信息
     */
    private HornAuditionTextResultVO text;

    /**
     * 音频内容信息
     */
    private HornAuditionAudioResultVO audio;
}
