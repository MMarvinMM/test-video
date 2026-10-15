package com.chinatower.platform.entity.device;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * 无人机机场
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UavNestDeviceInfoVO extends BaseDataScopeEntity {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "设备编码")
    @NotBlank(message = "设备编码不能为空！")
    private String deviceCode;

    @ApiModelProperty(value = "资源系统的设备编码")
    private String resourceDeviceCode;

    @ApiModelProperty(value = "设备名称")
    @NotBlank(message = "设备名称不能为空！")
    private String deviceName;

    @ApiModelProperty(value = "资源状态(0在线，1离线,2 从未连接)")
    private Integer resourceStatus;

    @ApiModelProperty(value = "所属站址")
    private String siteId;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "端口")
    private String port;

    @ApiModelProperty(value = "适用性(共享-1、独享-0)")
    private Long adaptability;

    @ApiModelProperty(value = "产权性质")
    private Long rightsProperties;

    @ApiModelProperty(value = "产权单位")
    private Long rightsUnit;

    @ApiModelProperty(value = "业务类型")
    private Long bussinessType;

    @ApiModelProperty(value = "资源类型")
    private Long resourceType;

    @ApiModelProperty(value = "设备类型;机巢设备")
    private Long deviceType;

    @ApiModelProperty(value = "设备厂家")
    private String manufacturerCode;

    @ApiModelProperty(value = "机巢型号")
    private String modelCode;

    @ApiModelProperty(value = "出保日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date expirationDate;

    @ApiModelProperty(value = "安装日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date installDate;

    @ApiModelProperty(value = "代维方式")
    private Integer maintenanceType;

    @ApiModelProperty(value = "代维厂家")
    private Long maintenanceManufacturer;

    @ApiModelProperty(value = "0-铁塔 1-机房 2-其他")
    private Integer installationsite;

    @ApiModelProperty(value = "传输方式")
    private Integer transmissionMode;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "入网时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date accessTime;

    @ApiModelProperty(value = "设备序列号")
    private String deviceSerialId;

    @ApiModelProperty(value = "挂高类型")
    private String hangingType;

    @ApiModelProperty(value = "挂高(米)")
    private Double hangingHeight;

    @ApiModelProperty(value = "重量")
    private Double weight;

    @ApiModelProperty(value = "接入协议")
    private String accessProtocols;

    @ApiModelProperty(value = "接入协议名称")
    private String accessProtocolsName;

    @ApiModelProperty(value = "传输协议")
    private String transportProtocols;

    @ApiModelProperty(value = "同步状态(0-设备未同步、3-同步平台成功、4-设备同步平台失败、5-设备连接失败、6-通道同步失败、9-设备同步完成)")
    private Integer synchState;

    @ApiModelProperty(value = "接入类型：0-私协接入，1-国标接入，2-主动注册，字典：access_type")
    private Integer accessType;

    @ApiModelProperty(value = "接入类型名称")
    private String accessTypeName;

    @ApiModelProperty(value = "周边环境")
    private String environment;

    @ApiModelProperty(value = "描述")
    private String desc;

    @ApiModelProperty(value = "接入节点（中心、下沉点）")
    private String accessNode;

    @ApiModelProperty(value = "归属组织")
    private String orgCode;

    @ApiModelProperty(value = "设备所属行业(大类)")
    private String deviceIndustry;

    @ApiModelProperty(value = "特色功能")
    private String features;

    @ApiModelProperty(value = "设备所属行业(小类)")
    private Integer deviceIndustrySub;

    @ApiModelProperty(value = "产品系列(1-可见光、2-双目热成像、3-激光夜视、4-全景监控)")
    private Integer productSeries;

    @ApiModelProperty(value = "同步动作 0不操作 1 新增 2 修改 3 删除")
    private Integer syncAction;

    @ApiModelProperty(value = "专属客户")
    private String customerId;

    @ApiModelProperty(value = "产权客户id")
    private String rightsCustomerId;

    @ApiModelProperty(value = "水平范围从")
    private Double horizonFrom;

    @ApiModelProperty(value = "水平范围到")
    private Double horizonTo;

    @ApiModelProperty(value = "垂直范围从")
    private Double verticalFrom;

    @ApiModelProperty(value = "垂直范围到")
    private Double verticalTo;

    @ApiModelProperty(value = "视角场从")
    private Double visualFrom;

    @ApiModelProperty(value = "视角场到")
    private Double visualTo;

    @ApiModelProperty(value = "平台编码")
    private String platformCode;

    @ApiModelProperty(value = "平台名称")
    private String platformName;

    @ApiModelProperty(value = "接入方式(0-直联、1-级联、2-NVR)")
    private String isCascade;

    @ApiModelProperty(value = "南向视频平台主键ID")
    private String southPlatformId;

    @ApiModelProperty(value = "级联平台主键")
    private Integer deviceCascadeId;

    @ApiModelProperty(value = "注册Id")
    private String registerId;

    @ApiModelProperty(value = "PMS立项单号")
    private String pmsRequestId;

    @ApiModelProperty(value = "累计离线时长")
    private String sumOfflineTime;

    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @ApiModelProperty(value = "0 运管页面新增，1微信小程序新增")
    private String addType;

    @ApiModelProperty(value = "合同编码")
    private String contractCode;

    @ApiModelProperty(value = "设备来源，1存量，2新建")
    private String deviceAttribute;

    @ApiModelProperty(value = "机巢SN编码")
    private String nestSn;

    @ApiModelProperty(value = "站址地址")
    private String address;

    @ApiModelProperty(value = "经度")
    private String longitude;

    @ApiModelProperty(value = "纬度")
    private String latitude;

    @ApiModelProperty(value = "所属组织")
    private Long dept;

    @ApiModelProperty(value = "行政区域")
    private String adminArea;

    @ApiModelProperty(value = "行业类别，字典:cus_industry_type")
    private String cusIndustryType;

    @ApiModelProperty(value = "属地化节点code")
    private String locationCode;

    @ApiModelProperty(value = "海拔高度")
    private Double altitude;

    @ApiModelProperty(value = "建议巡检半径（m）")
    private Double inspectionRadius;

    @ApiModelProperty(value = "应用修改标识（1:已修改 0未修改）")
    private String appModFlag;

    @ApiModelProperty(value = "运行状态 字典：device_uav_running_state")
    private String runningState;

    @ApiModelProperty(value = "省份编码")
    private String provinceCode;

    @ApiModelProperty(value = "省份名称")
    private String provinceName;

    @ApiModelProperty(value = "市区编码")
    private String cityCode;

    @ApiModelProperty(value = "市区名称")
    private String cityName;

    @ApiModelProperty(value = "区县编码")
    private String countyCode;

    @ApiModelProperty(value = "区县名称")
    private String countyName;

    @ApiModelProperty(value = "乡镇编码")
    private String townCode;

    @ApiModelProperty(value = "乡镇名称")
    private String townName;

    @ApiModelProperty(value = "厂家")
    private List<String> manufacturerCodeList;

    @ApiModelProperty(value = "修改开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTimeBegin;

    @ApiModelProperty(value = "修改结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTimeEnd;

    @ApiModelProperty(value = "创建开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTimeBegin;

    @ApiModelProperty(value = "创建结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTimeEnd;

    @ApiModelProperty(value = "接入节点名称")
    private String accessNodeName;

    @ApiModelProperty(value = "客户数")
    private Integer custCount;

    @ApiModelProperty(value = "省公司编码")
    private String provinceDeptCode;

    @ApiModelProperty(value = "省公司名称")
    private String provinceDeptName;

    @ApiModelProperty(value = "市公司编码")
    private String cityDeptCode;

    @ApiModelProperty(value = "市公司名称")
    private String cityDeptName;

    @ApiModelProperty(value = "节点名称/编号")
    private String accessCodeOrName;

    private Integer pageSize;

    private Integer pageNum;

    @ApiModelProperty(value = "是否展示无人机设备数据")
    private boolean needDevice = true;

    @ApiModelProperty(value = "是否分页")
    private Boolean enablePage;

    @ApiModelProperty("1-私有化")
    private String isLocation;

    private String modelName;

    private String manufacturerName;

    @ApiModelProperty(value = "属地化同步类型")
    private String opType;

    private String sort;

    private String sortName;

    /**
     * 是否位置固定 1 是  0 否
     */
    private Integer positionFix;

    /**
     * 经纬度更新时间
     */
    private Date latLonUpdateTime;
}
