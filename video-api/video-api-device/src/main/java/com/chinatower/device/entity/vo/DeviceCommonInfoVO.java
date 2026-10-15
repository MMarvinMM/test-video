package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.platform.entity.devicemgmt.vo.ChannelInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DeviceCommonInfoVO extends BaseDataScopeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备编码列表
     */
    private List<String> deviceCodeList;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备别名
     */
    private String deviceAlias;

    /**
     * 设备状态:0在线，1离线
     */
    private Integer deviceStatus;

    /**
     * 设备状态集合
     */
    private List<String> deviceStatusList;

    /**
     * 设备状态名称
     */
    private String deviceStatusName;

    /**
     * 省份
     */
    private String provinceId;
    /**
     * 省份编码
     */
    private String provinceCode;
    /**
     * 省份名称
     */
    private String provinceName;

    /**
     * 地市
     */
    private String cityCode;
    /**
     * 地市id
     */
    private String cityId;
    /**
     * 地市名称
     */
    private String cityName;

    /**
     * 区县
     */
    private String countyCode;
    /**
     * 区县id
     */
    private String countyId;
    /**
     * 区县名称
     */
    private String countyName;

    /**
     * 设备类型
     */
    private Integer categoryCode;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 设备类型集合
     */
    private List<String> deviceTypeList;

    /**
     * 设备类型名称
     */
    private String deviceTypeName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 业务系统站址编码
     */
    private Integer isCascade;

    /**
     * 业务系统站址名称
     */
    private String siteName;

    /**
     * 公共库站址编码
     */
    private String publicSiteCode;

    /**
     * 设备厂家code-物联设备、雷达
     * 设备型号编码-喇叭、执法记录仪、无人机
     */
    private String modelCode;

    /**
     * 设备厂家名称-物联设备、雷达
     * 设备型号名称-喇叭、执法记录仪、无人机
     */
    private String modelName;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date creatorDate;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private Date modifierDate;


    /**
     * 设备挂高，单位：米
     */
    private String height;

    /**
     * 站址地址
     */
    private String location;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 所属管理区域
     */
    private String areaCode;

    /**
     * 行政区域
     */
    private String admCode;

    /**
     * 物联设备型号编码
     */
    private String deviceModelCode;

    /**
     * 应用修改标识（1:已修改 0未修改）
     */
    private String appModFlag;

    /** 是否配置了算法 1是0否 */
    private String hasLinkConf;

    /** 接入协议*/
    private String access_protocols;
    /**
     * 展示组织名称
     */
    private String orgName;
    /**
     * 组织编码--执法记录仪、无人机
     */
    private String orgCode;
    /**
     * 组织关系ID--执法记录仪、无人机
     */
    private String orgRelId;

    /**
     * 接入类型:0-国标；1-onvif；3-私有协议-执法记录仪
     */
    private Integer collectType;

    /**
     * 部门编码
     */
    @ApiModelProperty(value = "部门编码")
    private List<String> deptIds;
    /**
     * 关联组织
     */
    private List<OrganDeviceRelVO> orgRels;
    /**
     * 监测因子数据
     */
    private List<SensorAttrVO> sensorAttrs;

    /**
     * 租户编码
     */
    private String tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 设备数量
     */
    private Integer deviceCount;

    /**
     * 版本编号
     */
    private String appVerCode;
    /**
     * 街道行政区域编码
     */
    private String townCode;
    /**
     * 街道行政区域名称
     */
    private String townName;
    /**
     * 订购状态
     */
    private String orderStatus;
    /**
     * 订购状态名称
     */
    private String orderStatusName;
    /**
     * 当前页
     */
    private Integer pageNum;
    /**
     * 页数限制
     */
    private Integer pageSize;

    /**
     * 排序方式
     * 排序字段
     * @return
     */
    private String sortName;

    /**
     * 排序规则
     */
    private String sort;

    /**
     * 雷达天线扫描周期 单位：秒
     */
    private String devicePeriod;

    /**
     *雷达覆盖范围
     */
    private String wZone;

    /**
     * 相控角度
     */
    private String parAngle;

    /**
     * 是否展示设备数据
     */
    private boolean needDevice = false;

    /**
     * 是查询收藏标志- 0 查询已收藏  1-查询所有
     */
    private String isMonitorFlag;

    /**
     * 是否收藏
     * 0未收藏 1已收藏
     */
    private String isMonitor = "0";

    /**
     * 子节点列表
     */
    private List<IotDeviceVO> list;

    /**
     * 编号或名称--APP使用
     */
    private String codeOrName;

    /**
     * 用户经度--APP使用
     */
    private String userLongitude;

    /**
     * 用户纬度--APP使用
     */
    private String userLatitude;

    /**
     * 用户离设备距离--APP使用
     */
    private Integer rangeMeter;

    /**
     * 距离
     */
    private String distance;

    /**
     * 设备厂家集合---APP使用
     */
    private List<String> modelCodeList;

    /**
     * 附件地址列表
     */
    private List<String> fileUrlList;

    /**
     * 设备类型列表
     */
    private List<String> deviceTypeCodeList;

    /**
     * 是否是导出excel
     */
    private boolean isExport = false;

    /**
     * app标识
     */
    private String appFlag;

    /**
     * 设备厂家编码-喇叭、执法记录仪、无人机
     */
    private String manufacturerCode;

    /**
     * 设备厂家名称-喇叭、执法记录仪、无人机
     */
    private String manufacturerName;

    /**
     * 通道--执法记录仪、无人机
     */
    private List<ChannelInfoVO> channelList;

    /**
     * 查询类型 1.无人机 2.执法记录仪 3.雷达  4.物联设备  5.大喇叭
     */
    private Integer queryType;

    /**
     * 站址编码
     */
    private String siteCode;

    /**
     * 机场名称
     */
    private String uavNestName;

    /**
     * 机场编号
     */
    private String uavNestCode;

    /**
     * 机场状态
     */
    private String uavNestStatus;

    /**
     * 机场厂家
     */
    private String uavNestManufacturerCode;

    /**
     * 机场厂家名称
     */
    private String uavNestManufacturerName;

    /**
     * 机场型号
     */
    private String uavNestModelCode;

    /**
     * 机场型号名称
     */
    private String uavNestModelName;

    /**
     * 机场位置是否固定
     */
    private Integer uavNestPositionFix;

    /**
     * 机场标定状态
     */
    private Integer uavNestCalibrationStatus;

    /**
     * 机场省份编码
     */
    private String uavNestProvinceCode;

    /**
     * 机场地市编码
     */
    private String uavNestCityCode;

    /**
     * 机场区县编码
     */
    private String uavNestCountyCode;

    /**
     * 机场乡镇编码
     */
    private String uavNestTownCode;

    /**
     * 机场经度
     */
    private String uavNestLongitude;

    /**
     * 机场纬度
     */
    private String uavNestLatitude;

    /**
     * 相机型号
     */
    private String cameraModelCode;

    /**
     * 相机型号名称
     */
    private String cameraModelName;

    /**
     * 海拔
     */
    private String altitude;

    /**
     *  是否有未完结告警（0-否、1-是）
     */
    private String isCompleteFlag;

    /**
     * ar标签是否收藏 0未收藏 1已收藏
     */
    private String tagCollect;

    /**
     * 无人机飞行状态
     */
    private String flyStatus;

    /**
     * 接入节点
     */
    private String accessNode;
}
