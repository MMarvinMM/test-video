package com.chinatower.device.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 无人机设备实体类
 */
@Data
public class UavDeviceDTO extends BaseDataScopeEntity implements Serializable {


    /**
     * 设备编码
     */
    @ApiModelProperty(value = "设备编码")
    @NotBlank(message = "摄像机编码不能为空!")
    private String deviceCode;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    @NotBlank(message = "摄像机名称不能为空!")
    private String deviceName;

    /**
     * 设备状态:0在线，1离线
     */
    @ApiModelProperty(value = "设备状态:0在线，1离线")
    private Integer deviceStatus;

    /**
     * 省份编码
     */
    @ApiModelProperty(value = "省份编码")
    private String provinceCode;

    /**
     * 地市编码
     */
    @ApiModelProperty(value = "地市编码")
    private String cityCode;

    /**
     * 区县编码
     */
    @ApiModelProperty(value = "区县编码")
    private String countyCode;

    /**
     * 乡镇编码
     */
    @ApiModelProperty(value = "乡镇编码")
    private String townCode;

    /**
     * 设备地址
     */
    @ApiModelProperty(value = "设备地址")
    private String location;

    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    private String longitude;

    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度")
    private String latitude;

    /**
     * 所属管理区域
     */
    @ApiModelProperty(value = "所属管理区域")
    private String areaCode;

    /**
     * 行政区域
     */
    @ApiModelProperty(value = "行政区域")
    private String admCode;

    /**
     * 设备厂家编码
     */
    @ApiModelProperty(value = "设备厂家编码")
    private String manufacturerCode;

    /**
     * 设备型号编码
     */
    @ApiModelProperty(value = "设备型号编码")
    private String modelCode;

    /**
     * 应用修改标识（1:已修改 0未修改）
     */
    @ApiModelProperty(value = "应用修改标识（1:已修改 0未修改）")
    private String appModFlag;

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页")
    private Integer pageNum = 1;

    /**
     * 每页显示条数
     */
    @ApiModelProperty(value = "每页显示条数")
    private Integer pageSize = 10;

    /**
     * 设备厂家名称
     */
    @ApiModelProperty(value = "设备厂家名称")
    private String manufacturerName;

    /**
     * 设备型号名称
     */
    @ApiModelProperty(value = "设备型号名称")
    private String modelName;

    /**
     * 设备类型
     */
    @ApiModelProperty(value = "设备类型")
    private String deviceType;

    /**
     * 订购状态;0-有效,1-失效,2-退订
     */
    @ApiModelProperty(value = "订购状态;0-有效,1-失效,2-退订")
    private Integer orderStatus;

    /**
     * 是否展示无人机设备数据
     */
    @ApiModelProperty(value = "是否展示无人机设备数据")
    private boolean needDevice = true;

    /**
     * 组织id
     */
    @ApiModelProperty(value = "组织id")
    private List<String> deptIds;

    /**
     * 组织编码
     */
    @ApiModelProperty(value = "组织编码")
    private String orgCode;

    /**
     * 关联组织
     */
    private List<OrganDeviceRelVO> orgRels;

    /**
     * 排序字段
     */
    @ApiModelProperty(value = "排序字段")
    private String orderField;

    /**
     * 排序规则
     */
    @ApiModelProperty(value = "排序规则")
    private String orderType;

    /**
     * 上传的附件列表
     */
    @ApiModelProperty(value = "上传的附件列表")
    private List<String> fileUrlList;

    /**
     * 编码或名称
     */
    @ApiModelProperty(value = "编码或名称")
    private String codeOrName;

    /**
     * 用户经度
     */
    @ApiModelProperty(value = "用户经度")
    private String userLongitude;

    /**
     * 用户纬度
     */
    @ApiModelProperty(value = "用户纬度")
    private String userLatitude;

    /**
     * 是查询收藏标志- 0 查询已收藏  1-查询所有
     */
    private String isMonitorFlag;

    /**
     * 是否APP调用
     */
    private String appflag;


    /**
     * 设备厂家集合-APP使用
     */
    private List<String> modelCodeList;


    /**
     * 设备状态集合
     */
    private List<String> deviceStatusList;

    /**
     * 设备类型名称
     */
    private String deviceTypeName;

    /**
     * 是否是导出excel
     */
    private boolean isExport = false;

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
     * 机场型号名称
     */
    @ApiModelProperty(value = "机场型号名称")
    private String uavNestModelName;

    /**
     * 相机型号
     */
    @ApiModelProperty(value = "相机型号")
    private String cameraModelCode;

    /**
     * 挂载型号
     */
    @ApiModelProperty(value = "挂载型号")
    private String uavMountModel;

    /**
     * 遥感器序列号
     */
    @ApiModelProperty(value = "遥感器序列号")
    private String rcNumber;

    /**
     * 设备序列号
     */
    @ApiModelProperty(value = "设备序列号")
    private String deviceSerialId;

    /**
     * 机场经度
     */
    @ApiModelProperty(value = "机场经度")
    private String nestLongitude;

    /**
     * 机场纬度
     */
    @ApiModelProperty(value = "机场纬度")
    private String nestLatitude;

    /**
     * 机场海拔
     */
    @ApiModelProperty(value = "机场海拔")
    private String nestAltitude;

    /**
     * 机场地址
     */
    @ApiModelProperty(value = "机场地址")
    private String nestAddress;

    /**
     * 机场设备类型
     */
    @ApiModelProperty(value = "机场设备类型")
    private String nestDeviceType;
    /**
     * 机场编号列表
     */
    private List<String> uavNestCodeList;

    /**
     * 负载枚举值
     */
    private String domain;

    /**
     * 设备类型
     */
    private String type;

    /**
     * 设备父类型
     */
    private String subType;

    /**
     * 飞行器类别
     */
    private String aircraftCategory;

    /**
     * 飞行器类型
     */
    private String aircraftType;

    /**
     * 起飞全重（kg)
     */
    private String modelWeight;

    /**
     * 支持相机
     */
    private String supportCamera;

    /**
     * 最大飞行时间（min）
     */
    private String maxFlyTime;

    /**
     * 最大水平速度（m/s）
     */
    private String maxLevelSpeed;

    /**
     * 厂家编码
     */
    private String modelManufacturerCode;

    /**
     * 厂家名称
     */
    private String modelManufacturerName;

    /**
     * 无人机编号列表
     */
    private List<String> deviceCodeList;

    /**
     * 挂载类型
     */
    private String mountType;

    /**
     * 挂载类型名称
     */
    private String mountTypeName;
}
