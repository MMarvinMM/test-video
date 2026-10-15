package com.chinatower.device.entity.vo;

import com.chinatower.platform.entity.devicemgmt.vo.ChannelInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 无人机返回对象
 */
@Data
public class UavDeviceQueryVO implements Serializable {

    @ApiModelProperty(value = "设备编码")
    private String deviceCode;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "设备状态:0在线，1离线")
    private Integer deviceStatus;

    @ApiModelProperty(value = "设备状态名称")
    private String deviceStatusName;

    @ApiModelProperty(value = "设备厂家编码")
    private String manufacturerCode;

    @ApiModelProperty(value = "设备型号编码")
    private String modelCode;

    @ApiModelProperty(value = "设备地址")
    private String location;

    @ApiModelProperty(value = "设备厂家名称")
    private String manufacturerName;

    @ApiModelProperty(value = "设备型号名称")
    private String modelName;

    @ApiModelProperty(value = "省份编码")
    private String provinceCode;

    @ApiModelProperty(value = "地市编码")
    private String cityCode;

    @ApiModelProperty(value = "区县编码")
    private String countyCode;

    @ApiModelProperty(value = "乡镇编码")
    private String townCode;

    @ApiModelProperty(value = "经度")
    private String longitude;
    @ApiModelProperty(value = "纬度")
    private String latitude;

    @ApiModelProperty(value = "所属管理区域")
    private String areaCode;;

    @ApiModelProperty(value = "行政区域")
    private String admCode;

    @ApiModelProperty(value = "设备类型")
    private String deviceType;

    @ApiModelProperty(value = "省份名称")
    private String provinceName;

    @ApiModelProperty(value = "地市名称")
    private String cityName;

    @ApiModelProperty(value = "区县名称")
    private String countyName;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "修改人姓名")
    private String modifierName;

    @ApiModelProperty(value = "创建人姓名")
    private String creatorName;

    @ApiModelProperty(value = "修改人")
    private String modifier;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "订购状态")
    private Integer orderStatus;

    @ApiModelProperty(value = "租户名称")
    private String tenantName;

    @ApiModelProperty(value = "订购状态名称")
    private String orderStatusName;

    @ApiModelProperty(value = "设备类型名称")
    private String deviceTypeName;

    @ApiModelProperty(value = "通道集合")
    private List<ChannelInfoVO> channelList;
    @ApiModelProperty(value = "设备所属组织编码")
    private String orgCode;
    @ApiModelProperty(value = "设备所属组织ID")
    private String orgRelId;
    @ApiModelProperty(value = "设备所属组织")
    private String orgName;

    @ApiModelProperty(value = "乡镇名称")
    private String townName;

    @ApiModelProperty(value = "是否收藏 0 未收藏 ；1 已收藏")
    private String isMonitor;

    @ApiModelProperty(value = "关联组织")
    private List<OrganDeviceRelVO> orgRels;

    @ApiModelProperty(value = "设备挂高")
    private String height;

    @ApiModelProperty(value = "站址编码")
    private String siteCode;

    @ApiModelProperty(value = "上传的附件列表")
    private List<String> fileUrlList;
    @ApiModelProperty(value = "距离")
    private String distance;

    @ApiModelProperty(value = "机场名称")
    private String uavNestName;

    @ApiModelProperty(value = "机场编号")
    private String uavNestCode;

    @ApiModelProperty(value = "机场状态")
    private String uavNestStatus;

    @ApiModelProperty(value = "机场厂家")
    private String uavNestManufacturerCode;

    @ApiModelProperty(value = "机场型号")
    private String uavNestModelCode;

    @ApiModelProperty(value = "相机型号")
    private String cameraModelCode;

    @ApiModelProperty(value = "机场厂家名称")
    private String uavNestManufacturerName;

    @ApiModelProperty(value = "机场型号名称")
    private String uavNestModelName;

    @ApiModelProperty(value = "机场状态名称")
    private String uavNestStatusName;

    @ApiModelProperty(value = "机场位置是否固定")
    private Integer uavNestPositionFix;

    @ApiModelProperty(value = "机场标定状态")
    private Integer uavNestCalibrationStatus;

    @ApiModelProperty(value = "机场省份编码")
    private String uavNestProvinceCode;

    @ApiModelProperty(value = "机场地市编码")
    private String uavNestCityCode;

    @ApiModelProperty(value = "机场区县编码")
    private String uavNestCountyCode;

    @ApiModelProperty(value = "机场乡镇编码")
    private String uavNestTownCode;

    @ApiModelProperty(value = "机场经度")
    private String uavNestLongitude;

    @ApiModelProperty(value = "机场纬度")
    private String uavNestLatitude;

    @ApiModelProperty(value = "相机型号名称")
    private String cameraModelName;

    @ApiModelProperty(value = "海拔")
    private String altitude;

    @ApiModelProperty(value = "接入节点")
    private String accessNode;

    /**
     * ar标签是否收藏 0未收藏 1已收藏
     */
    private String tagCollect;

    /**
     * 无人机飞行状态
     */
    private String flyStatus;
}
