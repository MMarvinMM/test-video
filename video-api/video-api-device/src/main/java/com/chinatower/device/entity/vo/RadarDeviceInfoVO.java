package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 物联设备表
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-07
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RadarDeviceInfoVO extends BaseDataScopeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 经度
     */
    private String longitude;
    /**
     * 纬度
     */
    private String latitude;
    /**
     * 高度
     */
    private String height;
    /**
     * 相机工作半径（Km）
     */
    private String workAreaRadius;
    /**
     * 水平角
     */
    private String pan;
    /**
     * 倾斜角
     */
    private String tilt;
    /**
     * 缩放值
     */
    private String zoom;
    /**
     * 聚焦值
     */
    private String focus;
    /**
     * 曝光度
     */
    private String brightness;
    /**
     * 对比度
     */
    private String contrast;
    /**
     * 自动调整水平角
     */
    private String autoPan;
    /**
     * 自动调整倾斜角
     */
    private String autoTilt;
    /**
     * 自动调整缩放值
     */
    private String autoZoom;
    /**
     * 自动调整聚焦值
     */
    private String autoFocus;
    /**
     *
     */
    private String autoIris;
    /**
     * 自动调整曝光度
     */
    private String autoBrightness;
    /**
     * 自动调整对比度
     */
    private String autoContrast;
    /**
     * 水平视场角
     */
    private String viewHorizon;
    /**
     * 垂直视场角
     */
    private String viewVertical;
    /**
     * 通道信息
     */
    private List<RadarChannelInfoVO> channels;
}
