package com.chinatower.device.entity.vo;

import lombok.Data;

@Data
public class VideoDirectionalInfoSmall {
    /**
     * 视频定向信息ID
     */
    private String id;

    /**
     * 时间戳
     */
    private String timeSpan;

    /**
     * POS信息X坐标
     */
    private Integer x;

    /**
     * POS信息Y坐标
     */
    private Integer y;

    /**
     * POS信息Z坐标
     */
    private Integer z;

    /**
     * 焦距
     */
    private Integer focalLength;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 宽
     */
    private Integer width;

    /**
     * 高
     */
    private Integer height;

    /**
     * 资源id
     */
    private String resourceId;

    /**
     * 是否删除
     */
    private Boolean isDeleted;

    /**
     * 俯仰角
     */
    private Integer omega;

    /**
     * 航向角
     */
    private Integer phi;

    /**
     * 翻滚角
     */
    private Integer kappa;

    /**
     * 视频文件id
     */
    private String videoFileId;

    /**
     * 模型矩阵
     */
    private String viewMatrix;

    /**
     * 投影矩阵
     */
    private String projectMatrix;

    /**
     * 渲染纹理文件
     */
    private String textureBaseFileId;

    /**
     * 是否关键帧
     */
    private Boolean keyFrame;
}