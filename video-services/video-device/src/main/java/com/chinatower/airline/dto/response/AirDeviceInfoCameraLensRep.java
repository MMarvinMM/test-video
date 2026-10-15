package com.chinatower.airline.dto.response;

import lombok.Data;

import java.util.List;

/**
 * @author csj
 * @description
 * @date 2024/11/4 19:58
 */
@Data
public class AirDeviceInfoCameraLensRep {

    /**
     * 相机镜头参数ID
     * */
    private String cameraLensConfigId;
    /**
     * 相机参数ID
     * */
    private String cameraConfigId;
    /**
     * 相机镜头名称
     * */
    private String cameraLensName;
    /**
     * 相机镜头类型
     * */
    private Integer cameraLensType;

    /**
     * 像元大小mm 宽传感器宽
     * */
    private Double sensorWide;

    /**
     * 传感器高
     * */
    private Double sensorHigh;
    /**
     * 像元大小宽度
     * */
    private Double pixelWide;
    /**
     * 像元大小高度
     * */
    private Double pixelHigh;
    /**
     * 焦距   0：定焦 1：变焦
     * */
    private Integer focalLength;

    /**
     * 变焦转换系数
     * */
    private Double zoomCoefficient;

    /**
     * 实际焦距范围mm 最大值
     * */
    private Double actualFocalLengthMax;

    /**
     * 实际焦距范围mm 最小值
     * */
    private Double actualFocalLengthMin;

    /**
     * 等效焦距范围mm 最大值
     * */
    private Double equivalentFocalLengthMax;

    /**
     *等效焦距范围mm 最小值
     * */
    private Double equivalentFocalLengthMin;

    /**
     * 光学变焦 最大值
     * */
    private Double opticsZoomMax;

    /**
     * 光学变焦 最小值
     * */
    private Double opticsZoomMin;
    /**
     * 数码变焦 最大值
     * */
    private Double digitalZoomMax;
    /**
     * 数码变焦 最小值
     * */
    private Double digitalZoomMin;

    /**
     * 镜头固定切换值 多个值逗号隔开
     * */
    private String fixedSwitchValue;

    /**
     * 是否支持夜景模式   0：不支持 1：支持
     * */
    private Integer nightModeStatus;

    /**
     * 是否支持红外分屏   0：不支持 1：支持
     * */
    private Integer infraredSplitScreenStatus;

    /**
     * 是否支持红外测温
     * */
    private Integer infraredThermometryStatus;

    /**
     * 调色板
     * */
    private List<String> lensPaletteList;

}
