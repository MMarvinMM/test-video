package com.chinatower.platform.entity.device;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.platform.entity.devicemgmt.vo.ChannelInfoVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 执法记录仪VO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RecorderDeviceVO extends BaseDataScopeEntity implements Serializable {


    /**
     * 主键id
     */
    private Long id;

    /**
     * 设备编号
     */
    @NotBlank(message = "设备编码不能为空!")
    private String deviceCode;

    /**
     * 设备名称
     */
    @NotBlank(message = "设备名称不能为空!")
    private String deviceName;

    /**
     * 设备状态
     */
    private Integer deviceStatus;

    /**
     * 设备厂家编码
     */
    private String manufacturerCode;

    /**
     * 设备型号编码
     */
    private String modelCode;

    /**
     * 订购状态
     */
    private Integer orderStatus;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 市编码
     */
    private String cityCode;

    /**
     * 区县编码
     */
    private String countyCode;

    /**
     * 乡镇编码
     */
    private String townCode;

    /**
     * 当前页
     */
    private Integer pageNum;

    /**
     * 每页显示条数
     */
    private Integer pageSize;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 设备地址
     */
    private String location;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 是否展示设备
     */
    private boolean needDevice;

    /**
     * 组织id
     */
    private List<String> deptIds;

    /**
     * 上传的附件列表
     */
    private List<String> fileUrlList;

    /**
     * 接入协议
     */
    private String accessProtocols;

    /**
     * 级联标识 0-直联---接入方式
     */
    private String isCascade;

    /**
     * 接入类型:0-国标；1-onvif；2-私有协议----执法记录仪目前只支持国标
     */
    private String collectType;

    /**
     * 设备登录用户名
     */
    private String devUserName;

    /**
     * 设备登录密码
     */
    private String devPassword;

    /**
     * 接入节点
     */
    private String accessNodeCode;

    /**
     * 区域编码集合
     */
    private List<Long> deptProvinceCity;

    /**
     * 选择组织id
     */
    private Long chooseOrgId;

    /**
     * 接入节点名称
     */
    private String accessName;

    /**
     * 管理区域
     */
    private String areaCode;

    /**
     * 行政区域
     */
    private String admCode;

    /**
     * 省名称
     */
    private String provinceName;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 区编码
     */
    private String countyName;

    /**
     * 街道编码
     */
    private String townName;

    /**
     * 厂家名称
     */
    private String manufacturerName;

    /**
     * 设备型号名称
     */
    private String modelName;

    /**
     * 是否查询收藏  0-收藏，1-所有
     */
    private String isMonitor;

    /**
     * 站址编码
     */
    private String siteCode;

    /**
     * 通道编码集合
     */
    private List<ChannelInfoVO> channelList;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 是否在通用修改
     */
    private String appModFlag;

    /**
     * 站址名称
     */
    private String siteName;

    /**
     * 操作类型 1：同步
     */
    @ApiModelProperty(value = "操作类型 1：同步")
    private Integer optType;

    /**
     * 运管同步-操作类型 0新增 1修改 2删除
     */
    private String opType;

    /**
     * 部门id
     */
    private String deptId;

    /**
     * 所属区域
     */
    private String adminArea;
    /**
     * 设备编码或名称
     */
    private String codeOrName;

    /**
     * 地址编码
     */
    private String locationCode;

    /**
     * 是否查询收藏  0-收藏，1-所有
     */
    private String isMonitorFlag;

    /**
     * 是否APP调用
     */
    private String appFlag;


    /**
     * 设备厂家集合-APP使用
     */
    private List<String> modelCodeList;


    /**
     * 设备状态集合
     */
    private List<String> deviceStatusList;

    /**
     * 用户经度
     */
    private String userLongitude;

    /**
     * 用户纬度
     */
    private String userLatitude;

    /**
     * 排序字段
     */
    private String orderField;

    /**
     * 设备产权
     */
    private String owner;

    /**
     * 接入方式 -0-ip接入；1-设备编号接入
     */
    private Long collectMethod;

    /**
     * 网域类型 -1-公网(默认)；0-局域内网
     */
    private Long netType;

    /**
     * 是否视频附件优先展示 true-是 false-不是
     */
    private Boolean isVideoFirst;

    /**
     * 设备编码集合
     */
    private List<String> deviceCodeList;

    /**
     * 国标编码
     */
    private String gb28181Code;
}
