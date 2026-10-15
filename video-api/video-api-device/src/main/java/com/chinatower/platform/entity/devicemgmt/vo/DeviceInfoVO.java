package com.chinatower.platform.entity.devicemgmt.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author chmod
 * @version 1.0
 * @date 2021/12/2 20:29
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DeviceInfoVO extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = 8609576632624208259L;

    /**
     * 租户编码
     */
    private String tenantId;
    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 设备编码
     */
    private String deviceCode;
    /**
     * 设备名称
     */
    private String devName;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 编辑前设备名称
     */
    private String oldDevName;
    /**
     * 设备状态
     */
    private String status;
    /**
     * 设备状态名称
     */
    private String deviceStatusName;
    /**
     * 设备地址
     */
    private String location;
    /**
     * 设备所属组织编码
     */
    private String orgCode;
    /**
     * 设备所属组织编码
     */
    private String oldOrgCode;
    /**
     * 设备所属组织ID
     */
    private String orgRelId;
    /**
     * 设备所属组织名称
     */
    private String orgName;
    /**
     * 订购状态
     */
    private String orderStatus;
    /**
     * 订购状态名称
     */
    private String orderStatusName;
    /**
     * 设备修改时间
     */
    private Date modifierDate;
    /**
     * 省份编码
     */
    private String provinceCode;
    /**
     * 省份名称
     */
    private String provinceName;
    /**
     * 地市编码
     */
    private String cityCode;
    /**
     * 地市名称
     */
    private String cityName;
    /**
     * 区县编码
     */
    private String countryCode;
    /**
     * 区县名称
     */
    private String countryName;
    /**
     * 乡镇(街道)编码
     */
    private String streetCode;
    /**
     * 乡镇(街道)名称
     */
    private String streetName;
    /**
     * 客户编码
     */
    private String custCode;

    /**
     * 客户名称
     */
    private String custName;

    /**
     * 站址编码
     */
    private String siteCode;
    /**
     * 站址名称
     */
    private String siteName;
    /**
     * 经度
     */
    private String longitude;
    /**
     * 纬度
     */
    private String latitude;
    
    /**
     * 客户数
     */
    private Integer custCount;
    /**
     * 通道信息集合
     */
    private List<ChannelInfoVO> children;
    
    /**
     * 设备编码集合
     */
    private List<String> deviceCodeList;
    
    /**
     * 租户ID集合
     */
    private Set<String> tenantIdSet;
    
    /**
     * 实时视频观看用户数
     */
    private String videoPlayNum;
    
    /**
     * 云台操作用户数
     */
    private String videoOperatorNum;
    
    /**
     * 操作类型：0-查询，1-编辑
     */
    private String operType;
    
    /**
     * 设备管理界面来源：1-运营管理-设备管理
     */
    private String devicePage;
    
    /**
     * 操作类型：0-铁塔，1-租户，2-铁塔视角下查看租户
     */
    private String source;
    
    /**
     * 通道名称
     */
    private String channelName;
    
    /**
     * 通道编码
     */
    private String channelCode;
    
    /**
     * 通道信息
     */
    private List<ChannelInfoVO> channelInfoList;

    /**
     * 部门编码集合
     */
    private List<String> deptIds;

    /**
     * 挂高
     */
    private String height;

    /**
     * 设备厂家code
     */
    private String modelId;

    /**
     * 设备厂家名称
     */
    private String modelName;
    /**
     * 距离
     */
    private String distance;
    /**
     * 页码
     */
    protected Integer pageNum;
    /**
     * 页长
     */
    protected Integer pageSize;

    /**
     * 摄像机类型
     */
    private int categoryCode;

    /**
     * 巡航类型 1:按时间段整合2:按预置位整合
     */
    private  String partrolMode;
    /**
     * 区县
     */
    private  String townCode;

    /**
     * 查询类型
     */
    private String queryType;

    /**
     * sdk通道信息集合
     */
    private List<ChannelInfoVO> channels;

    /**
     * 设备类型  1-摄像机 2-物联网设备
     */
    private String deviceType;

    /**
     * 摄像机型号编码
     */
    private String deviceModelCode;

    /**
     * 是否收藏字段，0未收藏 1已收藏
     */
    private String isMonitor;

    /**
     * 标签名称列表
     */
    private List<String> labelNameList;

    /**
     * 附件地址列表
     */
    private List<String> fileUrlList;

    /**
     * 接入节点
     */
    private String accessNode;
    /**
     * 本节点
     */
    private String platformCode;

    /**
     * 摄像机图标地址
     */
    private String imageUrl;


    private String defaultStatus;

    private String collectType;

    private String collectMethod;

    /**
     * 是否是导出excel
     */
    private boolean isExport = false;

    private Integer queryAddType;

    private String visualRadius;

    /**
     * 离线时长
     */
    private String offLineTime;

    /**
     * 协议类型
     *  1-rtsp；2-rtmp；3-hls；4-http-flv；5-wss； 6-铁塔私协
     * */
    private int protocolType;
    /**
     * 码流类型
     *  1-主码流；2-子码流
     * */
    private int streamType;
    /**
     * 网域类型
     *  拉流url的网域类型：1-公网(默认值为1)；0-局域内网；
     * */
    private String netType;

    List<DeviceUserHistoryVO> deviceInfoList;

    /**
     * 是否是铁塔账号 0否 1是
     */
    private String isTower;

    /**
     * 诊断结果状态（0：未诊断；1：正常；2：异常，其他值需结合业务扩展）
     */
    private Integer diagnoseResult;

    /**
     * 检测时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date diagnoseTime;


}
