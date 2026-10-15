package com.chinatower.video.modules.query.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeviceModel implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    public static final String MODELID_DAHUA = "dahua";
    public static final String MODELID_DHUA = "dhua";

    public static final String MODELID_HIKVISION = "hikvision";

    //属性 begin
    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 设备挂高，单位：米
     */
    private String height;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 设备型号ID.
     */
    private Long modelId;

    /**
     * 摄像机厂家编码.
     */
    private String mfgrCode;

    /**
     * 摄像机型号编码.
     */
    private String deviceModelCode;

    /**
     * 摄像机类型.
     */
    private String deviceType;

    /**
     * 图像传感器尺寸，英寸.
     */
    private String picSensorSize;

    /**
     * 图像传感器长度，毫米.
     */
    private String picSensorLength;

    /**
     * 图像传感器宽度，毫米.
     */
    private String picSensorWidth;

    /**
     * 最小焦距.
     */
    private String minFocus;

    /**
     * 焦距范围.
     */
    private String focusRange;

    /**
     * 水平视场角.
     */
    private String horizViewRange;

    /**
     * 垂直视场角.
     */
    private String vertiViewRange;

    /**
     * 水平角范围.
     */
    private String horizRange;

    /**
     * 俯仰角范围.
     */
    private String vertiRange;

    /**
     * 水平向下为负 0否1是.
     */
    private String vertiDownNeg;

    /**
     * 备注.
     */
    private String remark;

    /**
     * 可视距离.
     */
    private String visualRange;
    /**
     * 水平参数p0.
     */
    private BigDecimal pan;

    /**
     * 垂直参数t0.
     */
    private BigDecimal tilt;

    private String siteCode;

    private int distance;
}
