package com.chinatower.device.dto;

import lombok.Data;

/**
 * @author willem
 * @date 2024/10/22
 * @description
 */
@Data
public class CreateTextRequestDTO {
    /**
     * 区域编码
     */
    private String regionCode;
    /**
     * 语速
     */
    private Integer speechRate;
    /**
     * 语调音高
     */
    private Integer tonePitch;
    /**
     * 文本内容
     */
    private String txtContent;
    /**
     * 文本标题
     */
    private String txtName;
    /**
     * 音量
     */
    private Integer volume;
    /**
     * 引擎类型
     */
    private String xunfeiEnt = "intp65";
    /**
     * 发音人
     */
    private String xunfeiVcn;
    /**
     * 接入节点
     */
    private String accessNode;
    /**
     * 受理节点
     */
    private String acceptNode;
}
