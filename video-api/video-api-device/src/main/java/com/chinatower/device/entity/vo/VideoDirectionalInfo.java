package com.chinatower.device.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VideoDirectionalInfo {
    /**
     * 视频定向信息ID
     */
    @JsonProperty("Id")
    private String Id;

    /**
     * 时间戳
     */
    @JsonProperty("TimeSpan")
    private String TimeSpan;

    /**
     * POS信息X坐标
     */
    @JsonProperty("X")
    private Integer X;

    /**
     * POS信息Y坐标
     */
    @JsonProperty("Y")
    private Integer Y;

    /**
     * POS信息Z坐标
     */
    @JsonProperty("Z")
    private Integer Z;

    /**
     * 焦距
     */
    @JsonProperty("FocalLength")
    private Integer FocalLength;

    /**
     * 创建时间
     */
    @JsonProperty("CreateTime")
    private String CreateTime;

    /**
     * 宽
     */
    @JsonProperty("Width")
    private Integer Width;

    /**
     * 高
     */
    @JsonProperty("Height")
    private Integer Height;

    /**
     * 资源id
     */
    @JsonProperty("ResourceId")
    private String ResourceId;

    /**
     * 是否删除
     */
    @JsonProperty("IsDeleted")
    private Boolean IsDeleted;

    /**
     * 俯仰角
     */
    @JsonProperty("Omega")
    private Integer Omega;

    /**
     * 航向角
     */
    @JsonProperty("Phi")
    private Integer Phi;

    /**
     * 翻滚角
     */
    @JsonProperty("Kappa")
    private Integer Kappa;

    /**
     * 视频文件id
     */
    @JsonProperty("VideoFileId")
    private String VideoFileId;

    /**
     * 模型矩阵
     */
    @JsonProperty("ViewMatrix")
    private String ViewMatrix;

    /**
     * 投影矩阵
     */
    @JsonProperty("ProjectMatrix")
    private String ProjectMatrix;

    /**
     * 渲染纹理文件
     */
    @JsonProperty("TextureBaseFileId")
    private String TextureBaseFileId;

    /**
     * 是否关键帧
     */
    @JsonProperty("KeyFrame")
    private Boolean KeyFrame;
}
