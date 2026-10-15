package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class DeviceRequestVO extends BaseDataScopeEntity implements Serializable {
    /**
     * 设备类型：1-IPC 0-NVR;2-AR摄像头；3-热成像摄像头；4-人脸卡口；5-车辆卡口；6-卡口主机
     */
    @ApiModelProperty("设备类型")
    private Integer categoryCode;
    @ApiModelProperty("设备编码")
    private String deviceCode;
    @ApiModelProperty("通道编码")
    private String channelCode;
    @ApiModelProperty("站址编码")
    private String siteCode;
    @ApiModelProperty("站址名称")
    private String siteName;
    @ApiModelProperty("经度查询参数")
    private String longitude;
    @ApiModelProperty("纬度查询参数")
    private String latitude;
    @ApiModelProperty("站址编码集合")
    private List<String> siteCodes;
    @ApiModelProperty(value = "省编码")
    private String provinceId;
    @ApiModelProperty(value = "市编码")
    private String cityId;
    @ApiModelProperty(value = "区编码")
    private String countyId;
    @ApiModelProperty(value = "摄像机编码列表")
    private List<String> deviceCodes;
    @ApiModelProperty(value = "检索半径，单位：米")
    private String radius;
}
