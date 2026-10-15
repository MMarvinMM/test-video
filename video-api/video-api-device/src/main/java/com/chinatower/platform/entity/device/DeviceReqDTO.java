package com.chinatower.platform.entity.device;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @ClassName DeviceInfoDTO
 * @Description 设备请求实体类
 * @Author chensw
 * @Date 2022/1/18
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceReqDTO extends BaseDataScopeEntity implements Serializable {
    /**
     * 设备编码
     * 行政区划树、组织树、标签树时，非必填
     * @required true
     */
    @ApiModelProperty(value = "设备编码")
    private String deviceCode;
    /**
     * 通道编码
     */
    @ApiModelProperty(value = "通道编码")
    private String channelCode;
    /**
     * 摄像头状态
     */
    @ApiModelProperty(value = "摄像头状态")
    private String status;
    /**
     * 预置点id
     */
    @ApiModelProperty(value = "预置点id")
    private String provinceId;
    /**
     * 省级编码
     */
    @ApiModelProperty(value = "省级编码")
    private String cityId;
    /**
     * 市级编码
     */
    @ApiModelProperty(value = "市级编码")
    private String countyId;
    /**
     * 街道编码
     */
    private String townCode;
    /**
     * 区级编码
     */
    @ApiModelProperty(value = "区级编码")
    private Set<String> orgIdList;
    /**
     * 部门编码
     */
    @ApiModelProperty(value = "部门编码")
    private List<String> deptIds;
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private List<String> userIds;
    /**
     * 客户编码
     */
    @ApiModelProperty(value = "客户编码")
    private List<String> tenantIds;

    /** 客户编码 */
    protected transient List<String> customerIds;
    /** 行业编码 */
    @ApiModelProperty(value = "行业编码")
    private String appVerCode;
    /** 厂家信息 */
    @ApiModelProperty("厂家信息")
    private String modelCode;

    /**
     * 是否查询收藏  0-收藏，1-所有
     */
    @ApiModelProperty("收藏标签")
    private String isMonitorFlag;

    /**
     * 不传 或者 false 都是查询有效订购设备，传入 true 查询所有订购设备
     */
    @ApiModelProperty("查询全部标识")
    private Boolean allOrderFlag;
    /** 纬度 */
    @ApiModelProperty("纬度")
    private String latitude;
    /** 经度 */
    @ApiModelProperty("经度")
    private String longitude;
    /** 距离 */
    @ApiModelProperty("距离")
    private String distance;

    /**
     * 当 nearest 不为空时 取最接近的一条数据
     */
    @ApiModelProperty("距离最近的")
    private String nearest;
    /** 设备编码列表 */
    @ApiModelProperty(value = "设备编码列表")
    private List<String> deviceCodes;

    /**
     * 设备编码列表
     */
    private List<String> deviceCodeList;

    /**
     * 开始日期
     * 行政区划树、组织树、标签树时，非必填
     * @required true
     */
    @ApiModelProperty("开始日期")
    private String startDate;

    /**
     * 结束日期
     * 行政区划树、组织树、标签树时，非必填
     * @required true
     */
    @ApiModelProperty("结束日期")
    private String endDate;
    /** 页数 */
    @ApiModelProperty(value = "页数")
    private Integer pageNo = 0;
    /** 页数 */
    private Integer pageNum;
    /** 分页数 */
    @ApiModelProperty(value = "分页数")
    private Integer pageSize = 10;

    /** 是否返回设备 */
    @ApiModelProperty("是否返回设备")
    private Boolean backDevFlag;

    /**
     * 设备类型:1-IPC 0-NVR;2-AR摄像头；3-热成像摄像头
     */
    @ApiModelProperty(value = "设备类型")
    private Integer categoryCode;

    /**
     * 设备类型:1-IPC 0-NVR;2-AR摄像头；3-热成像摄像头
     */
    @ApiModelProperty(value = "设备类型")
    private List<Integer> categoryCodeList;
    /** 在离线状态 */
    @ApiModelProperty(value = "在离线状态")
    private List<Integer> statusList;

    /**
     * keywords 关键字
     */
    @ApiModelProperty(value = "关键字")
    private String keywords;

    /**
     * 在线数量统计维度，不传就是通道维度，1为设备维度
     */
    @ApiModelProperty(value = "在线数量统计维度，不传就是通道维度，1为设备维度")
    private Integer deviceOrChannel;

    /**
     * 查询频次--不传或传"null"-实时 1-分 2-时 3-天
     */
    private String timeFrequency;

    /**
     * 设备类型集合
     */
    private List<String> deviceTypeList;

    /**
     * 设备状态集合
     */
    private List<String> deviceStatusList;

    /**
     * 是否返回设备
     */
    private boolean needDevice = false;

    /**
     * 查询类型
     */
    private Integer queryType;

    /**
     * 设备状态
     */
    private Integer deviceStatus;

    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 是否有联动配置
     */
    private String hasLinkConf;

    /**
     * 设备类型
     */
    @ApiModelProperty(value = "设备类型")
    private String deviceType;

    /**
     * 设备厂家编码
     */
    @ApiModelProperty(value = "设备厂家编码")
    private String manufacturerCode;

    /**
     * 事件id
     */
    private String eventId;

    /**
     * 机场名称
     */
    @ApiModelProperty(value = "机场名称")
    private String uavNestName;

    /**
     * 机场编号
     */
    @ApiModelProperty(value = "机场编号")
    private String uavNestCode;

    /**
     * 机场状态
     */
    @ApiModelProperty(value = "机场状态")
    private String uavNestStatus;

    /**
     * 机场状态列表
     */
    @ApiModelProperty(value = "机场状态列表")
    private List<String> uavNestStatusList;

    /**
     * 机场厂家
     */
    @ApiModelProperty(value = "机场厂家")
    private String uavNestManufacturerCode;

    /**
     * 机场型号
     */
    @ApiModelProperty(value = "机场型号")
    private String uavNestModelCode;

    /**
     * 相机型号
     */
    @ApiModelProperty(value = "相机型号")
    private String cameraModelCode;

    /**
     * ar标签收藏标识
     */
    private Integer arTagFlag;

    /**
     * ar标签是否收藏标识0显示收藏
     */
    private String isArTagFlag;

    /**
     * 机场编号列表
     */
    private List<String> uavNestCodeList;

    /**
     * 设备来源
     */
    private String deviceSource;


    /**
     * 订购标识 1：已订购  0 未订购
     */
    private String ifOrderFlag;

    private List<String> tenantIdList;

    /**
     * 是否查询无人机飞行状态
     */
    private Boolean withFlightStatus;

    private List<String> deviceList;

    /**
     * 不包含的机巢编码
     */
    private List<String> notIncludeNestCodeList;

    /**
     * 不包含单无人机 true - 不包含 false - 包含
     */
    private Boolean withoutSingleUav;

    /**
     * 单机巢分支数据权限片段。
     * queryUavInfoList 的 SQL 为 UNION ALL，下半部分（单机巢，别名 s/z）用它做数据权限过滤，
     * 由 UavNestDataScopeHelper 生成后写入。默认空串，保证 ${param.nestDataScope} 不会渲染出 null。
     */
    private String nestDataScope = "";

}