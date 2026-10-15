package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 物联设备表
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-07
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class IotDeviceVO extends BaseDataScopeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备id
     */
    private String iotDeviceId;

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
     * 省份名称
     */
    private String provinceName;

    /**
     * 地市
     */
    private String cityId;
    /**
     * 地市名称
     */
    private String cityName;

    /**
     * 区县
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

    private List<String> radarDeviceTypeList;
    private List<String> iotDeviceTypeList;

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
    private String siteCode;

    /**
     * 业务系统站址名称
     */
    private String siteName;

    /**
     * 公共库站址编码
     */
    private String publicSiteCode;

    /**
     * 设备厂家code
     */
    private String modelCode;

    /**
     * 设备厂家名称
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

    /**
     * 接入协议
     */
    private String access_protocols;
    /**
     * 展示组织名称
     */
    private String orgName;

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
     * 租户id
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
     * 版本号
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
     * 1-vm_iot_device_t  2-vm_uav_device_info_t  3-vm_horn_device_info_t 4-vm_recorder_device_info_t
     */
    private String flag;

    /**
     * ar标签标识
     */
    private Integer arTagFlag;

    /**
     * ar标签是否收藏标识0显示收藏
     */
    private String isArTagFlag;

    /**
     * ar标签是否收藏 0未收藏 1已收藏
     */
    private String tagCollect;
}
