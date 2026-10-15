package com.chinatower.platform.entity.device;

import lombok.Data;

/**
 * 文本标签实体类
 */
@Data
public class SizeVO {
    /**
     * 文本标签宽度
     */
    private String width;

    /**
     * 文本标签高度
     */
    private String height;
    /**
     * 百分比宽度
     */
    private String percentWidth;

    /**
     * 百分比高度
     */
    private String percentHeight;

    private String lon;

    private String lat;
}
