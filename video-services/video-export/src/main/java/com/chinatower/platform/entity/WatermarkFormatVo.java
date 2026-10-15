package com.chinatower.platform.entity;

import lombok.Data;

/**
 * @Author: songxinfeng
 * @Date: 2024/6/5 14:46
 */

@Data
public class WatermarkFormatVo{

    /**
     * 水印字体大小
     * */
    private Integer fontSize;

    /**水印内容
     * */
    private String watermarkText;

    /**
     * 水印透明度
     * */
    private Float transparency;

    /**
     * 水印密度垂直向上间距
     * */
    private Integer height;

    /**
     * 水印密度水平向上间距
     * */
    private Integer width;

    /**
     * 水印密度（1小 2中 3大）  默认为中
     * */
    private String densityType;
}
