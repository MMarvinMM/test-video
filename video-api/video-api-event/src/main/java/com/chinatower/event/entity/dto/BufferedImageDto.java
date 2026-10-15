package com.chinatower.event.entity.dto;

import lombok.Data;

import java.awt.image.BufferedImage;

@Data
public class BufferedImageDto {

    /**
     * 图片对象
     */
    private BufferedImage image;

    /**
     * 图片名称
     */
    private String imgName;
}
