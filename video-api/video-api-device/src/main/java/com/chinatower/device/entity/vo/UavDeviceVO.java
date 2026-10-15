package com.chinatower.device.entity.vo;

import com.chinatower.platform.entity.devicemgmt.vo.ChannelInfoVO;
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
public class UavDeviceVO implements Serializable {

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

    @ApiModelProperty(value = "客户数")
    private String custCount;

    @ApiModelProperty(value = "设备厂家名称")
    private String manufacturerName;

    @ApiModelProperty(value = "设备型号名称")
    private String modelName;

    @ApiModelProperty(value = "市公司编码")
    private String cityDeptCode;

    @ApiModelProperty(value = "市公司名称")
    private String cityDeptName;

    @ApiModelProperty(value = "省公司编码")
    private String provinceDeptCode;

    @ApiModelProperty(value = "省公司名称")
    private String provinceDeptName;

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
    private String areaCode;

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

    @ApiModelProperty(value = "租户id")
    private String tenantId;

    @ApiModelProperty(value = "订购状态名称")
    private String orderStatusName;

    @ApiModelProperty(value = "设备类型名称")
    private String deviceTypeName;

    @ApiModelProperty(value = "乡镇名称")
    private String townName;

    @ApiModelProperty(value = "通道集合")
    private List<ChannelInfoVO> channelList;

    @ApiModelProperty(value = "机场SN编码")
    private String airportCode;

    @ApiModelProperty(value = "挂高(米)")
    private Double hangingHeight;

    @ApiModelProperty(value = "挂高类型")
    private String hangingType;

    @ApiModelProperty(value = "设备序列号")
    private String deviceSerialId;

    @ApiModelProperty(value = "安装日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date installDate;

    @ApiModelProperty(value = "适用性(共享-1、私有-0)")
    private Integer adaptability;

    @ApiModelProperty(value = "重量")
    private Double weight;

    @ApiModelProperty(value = "接入协议")
    private String accessProtocols;

    @ApiModelProperty(value = "接入节点（中心、下沉点）")
    private String accessNodeCode;

    private String isMonitor;

    private String siteCode;

    @ApiModelProperty(value = "挂高(米)")
    private Double height;

    private String locationCode;

    private String locationName;

    private String distance;

    @ApiModelProperty(value = "附件信息")
    private List<String> fileUrlList;

    /**
     * 应用修改标识（1:已修改 0未修改）
     */
    private String appModFlag;

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

    @ApiModelProperty(value = "相机型号名称")
    private String cameraModelName;

    @ApiModelProperty(value = "海拔")
    private String altitude;

    @ApiModelProperty(value = "无人机SN编码")
    private String uavCode;

    @ApiModelProperty(value = "起飞权重（kg）")
    private String takeoffWeight;

    @ApiModelProperty(value = "遥感器序列号")
    private String rcNumber;

    @ApiModelProperty(value = "民航局实名登记码")
    private String registerCode;

    @ApiModelProperty(value = "无人机挂载型号")
    private String uavMountModel;

    @ApiModelProperty(value = "无人机挂载型号名称")
    private String uavMountModelName;

    @ApiModelProperty(value = "接入类型：0-私协接入，1-国标接入，2-主动注册，字典：access_type")
    private String accessType;

    @ApiModelProperty(value = "接入类型名称")
    private String accessTypeName;

    @ApiModelProperty(value = "接入协议名称")
    private String accessProtocolsName;
}
