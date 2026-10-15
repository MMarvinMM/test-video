package com.chinatower.device.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * @author csj
 * @description
 * @date 2024/11/4 14:37
 */
@Data
public class AirResultRenameReq {
    /**
     * 成果物ID
     * */
    private String id;
    /**
     * 重命名名称
     * */
    private String name;
    /**
     * 授理节点
     * */
    private String acceptNode;

    private String accessNode;

    /**
     * photo-图片
     * video-视频
     * */
    private String dataType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 水印开关
     */
    private String isWatermark;

    /**
     * 水印
     */
    private String watermark;

    /**
     * 水印
     */
    private String watermarkUrl;

    private String altitude;

    private Double height;

    /**
     * 道路信息
     */
    private String roadInfo;

}
