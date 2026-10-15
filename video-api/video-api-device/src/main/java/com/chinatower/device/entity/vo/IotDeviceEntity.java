package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 物联设备信息
 * @author zhujun
 * @date 2022-11-08
 */
@Data
public class IotDeviceEntity extends BaseDataScopeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备编码
     */
    @ApiModelProperty("设备编码")
    private String deviceCode;

    /**
     * 设备类型，运管同步，各系统和运管同步
     */
    @ApiModelProperty("设备类型")
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
     * 设备厂家code
     */
    @ApiModelProperty("设备厂家code")
    private String modelCode;

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
     * 物联设备型号编码
     */
    @ApiModelProperty("物联设备型号编码")
    private String deviceModelCode;

    /**
     * 组织机构ID
     */
    @ApiModelProperty("组织机构ID")
    private String deptId;

}
