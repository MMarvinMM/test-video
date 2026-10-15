package com.chinatower.device.entity.dto;

import lombok.Data;

/**
 * @author willem
 * @date 2024/10/22
 * @description 试听入参
 */
@Data
public class VmHornAuditionRequestDTO {
    /**
     * 试听内容类型 0-音频 1-文本
     */
    private Integer contentType;
    /**
     * 语速0-100
     */
    private Integer speechRate;

    /**
     * 语调音高0-100
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
     * 音量0-100
     */
    private Integer volume;
    /**
     * 发音人 中英-女:x2_yezi   中英-男:x2_qige
     */
    private String xunfeiVcn;

    /**
     * 资源id
     */
    private String resourceId;

    /**
     * 内容id
     */
    private Long id;
}
