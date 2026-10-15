package com.chinatower.sync.consumer.item;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UavNestDeviceReport implements Serializable {

    @ApiModelProperty(value = "设备编码")
    private String deviceCode;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "设备状态")
    private Integer resourceStatus;

    @ApiModelProperty(value = "设备厂家")
    private String manufacturer;

    @ApiModelProperty(value = "设备型号")
    private String deviceModel;

    @ApiModelProperty(value = "海拔")
    private String altitude;

    @ApiModelProperty(value = "删除标志, 0：正常，2：删除")
    private String delFlag;

    private String resourceDeviceCode;
    private String siteId;
    private String ip;
    private String port;
    private String account;
    private String password;
    private Long adaptability;
    private Long rightsProperties;
    private Long rightsUnit;
    private Long bussinessType;
    private Long resourceType;
    private Long deviceType;
    private Date expirationDate;
    private Date installDate;
    private Long maintenanceType;
    private Long maintenanceManufacturer;
    private Integer installationsite;
    private Integer transmissionMode;

    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
    private String remark;
    private Date accessTime;
    private String deviceSerialId;
    private String hangingType;
    private String hangingHeight;
    private String weight;
    private String accessProtocols;
    private String transportProtocols;
    private Integer synchState;
    private Integer accessType;
    private String environment;
    private String desc;
    private String accessNode;
    private String orgCode;
    private String deviceIndustry;
    private String features;
    private Integer deviceIndustrySub;
    private Integer productSeries;
    private Integer syncAction;
    private String customerId;
    private String rightsCustomerId;
    private String horizonFrom;
    private String horizonTo;
    private String verticalFrom;
    private String verticalTo;
    private String visualFrom;
    private String visualTo;
    private String platformCode;
    private String platformName;
    private String isCascade;
    private String southPlatformId;
    private Long deviceCascadeId;
    private String registerId;
    private String pmsRequestId;
    private String sumOfflineTime;
    private String projectId;
    private String addType;
    private String deviceAttribute;
    private String nestSn;
    private String address;
    private String longitude;
    private String latitude;
    private Long dept;
    private String adminArea;
    private String cusIndustryType;
    private String locationCode;
    private String secretKey;
    private String inspectionRadius;
    private String runningState;

    /**
     * 是否位置固定 1 是  0 否
     */
    private Integer positionFix;

    //canal类型
    private String canalType;
    // 执行时间
    private long ts;

    @ApiModelProperty(value = "省份编码")
    private String provinceCode;

    @ApiModelProperty(value = "地市编码")
    private String cityCode;

    @ApiModelProperty(value = "区县编码")
    private String countyCode;

    @ApiModelProperty(value = "乡镇编码")
    private String townCode;

    /**
     * 经纬度更新时间
     */
    private Date latLonUpdateTime;
}
