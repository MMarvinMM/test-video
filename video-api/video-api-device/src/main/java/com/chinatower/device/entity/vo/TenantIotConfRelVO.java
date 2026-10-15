package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 物联设备联动关联表
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-07
 */
@Data
public class TenantIotConfRelVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 物联设备联动关联ID
     */
    private Long confRelId;

    private String confRelName;

    /**
     * 物联设备联动ID
     */
    private Long iotDeviceConfId;

    /**
     * 摄像机设备编码
     */
    private String deviceCode;
    /**
     * 摄像机设备名称
     */
    private String deviceName;

    /**
     * 摄像机通道编码
     */
    private String channelCode;

    /**
     * 摄像机通道名称
     */
    private String channelName;

    /**
     * 预置位id
     */
    private Long presetId;

    private String presetName;

    /**
     * 水平参数[0, 3600]
     */
    private Float pan;

    /**
     * 垂直参数[-1800, 1800]
     */
    private Float tilt;

    /**
     * 变倍参数[0, 128]
     */
    private Float zoom;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;
    /**
     * 地址
     */
    private String location;
    /**
     * 厂商
     */
    private String modelCode;

    /**
     * 设备挂高，单位：米
     */
    private String height;

    /**
     * 联动摄像机类型1:指定摄像机2:最近的摄像机3:周边指定范围摄像机
     */
    private String linkConfType;

    /**
     * 摄像机图标地址
     * */
    private String deviceImageUrl;
}
