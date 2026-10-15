package com.chinatower.device.dto;

import lombok.Data;

/**
 * @author willem
 * @date 2024/10/10
 * @description
 */
@Data
public class HornAuditionDTO {

    /**
     * 发音人	xunfeiVcn	Y	String	中英-女:x2_yezi 中英-男:x2_qige
     */
    private String xunfeiVcn = "x2_qige";

    /**
     * 语速	speechRate	Y	Integer	语速0~100
     */
    private Integer speechRate = 50;

    /**
     * 语调音高	tonePitch	Y	Integer	语调音高0~100
     */
    private Integer tonePitch = 50;

    /**
     * 音量	volume	Y	Integer	0~100数值越大，音量越大
     */
    private Integer volume = 50;

    /**
     * 文本内容
     */
    private String txtContent;

    /**
     * 文本标题
     */
    private String txtName;

    /**
     * 固定值:intp65
     */
    private String xunfeiEnt = "intp65";
}
