package com.chinatower.device.dto;

import lombok.Data;

/**
 * @author willem
 * @date 2024/10/10
 * @description
 */
@Data
public class HornContentAuditionRequestDTO {

    /**
     * 内容id
     */
    private Long resourceId;
    /**
     * 内容类型 0-音频 1-文本
     */
    private Integer contentType;

    /**
     * 发音人	xunfeiVcn	Y	String	中英-女:x2_yezi 中英-男:x2_qige
     */
    private String voiceType;

    /**
     * 语速	speechRate	Y	Integer	语速0~100
     */
    private Integer speechRate;

    /**
     * 语调音高	tonePitch	Y	Integer	语调音高0~100
     */
    private Integer tonePitch;

    /**
     * 音量	volume	Y	Integer	0~100数值越大，音量越大
     */
    private Integer volume;
}
