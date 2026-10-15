package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description 物联设备信息
 * @author zhujun
 * @date 2022-11-08
 */
@Data
public class IotDeviceAreaEntity extends BaseDataScopeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备编码
     */
    @ApiModelProperty("设备编码")
    private String deviceCode;

    /**
     * 设备类型，运管同步，各系统和运管同步
     */
    @ApiModelProperty("设备类型，运管同步，各系统和运管同步")
    private String deviceType;

    /**
     * 设备名称
     */
    @ApiModelProperty("设备名称")
    private String deviceName;

    /**
     * 设备状态:0在线，1离线
     */
    @ApiModelProperty("设备状态:0在线，1离线")
    private int deviceStatus;

    /**
     * 设备挂高，单位：米
     */
    @ApiModelProperty("设备挂高，单位：米")
    private String height;

    /**
     * 站址地址
     */
    @ApiModelProperty("站址地址")
    private String location;

    /**
     * 经度
     */
    @ApiModelProperty("经度")
    private String longitude;

    /**
     * 纬度
     */
    @ApiModelProperty("纬度")
    private String latitude;

    /**
     * 省份
     */
    @ApiModelProperty("省份")
    private String provinceId;

    /**
     * 地市
     */
    @ApiModelProperty("地市")
    private String cityId;

    /**
     * 区县
     */
    @ApiModelProperty("区县")
    private String countyId;

    /**
     * 乡镇编码
     */
    @ApiModelProperty("乡镇编码")
    private String townCode;

    @ApiModelProperty("摄像机设备")
    private List<DeviceEntity> devices;
}
