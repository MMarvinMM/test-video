package com.chinatower.industry.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Name IndustryDeviceTree
 * @Description
 * @Author Chensw
 * @Date 2022/03/2022/3/23 14 : 29
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class IndustryDeviceTree {


    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 通道状态
     */
    private Integer channelStatus;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 站址编码
     */
    private String siteCode;

    /**
     * 省编码
     */
    private String provinceId;

    /**
     * 省名称
     */
    private String provinceName;

    /**
     * 市编码
     */
    private String cityId;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 区编码
     */
    private String countyId;

    /**
     * 区名称
     */
    private String countyName;

    /**
     * 通道在线数量
     */
    private Long onLineNumber;
    /**
     * 通道全部数量
     */
    private Long number;

    /**
     * 摄像机在线数量
     */
    private Long devOnLineNumber;
    /**
     * 摄像机全部数量
     */
    private Long devNumber;

    /**
     * 部门id
     */
    private String deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 标签id
     */
    private String labelId;

    /**
     * 标签名称
     */
    private String labelName;

    /**
     * 站址名称
     */
    private String siteName;

    /**
     * 距离
     */
    private String distance;

    /**
     * 挂高
     */
    private String height;

    /**
     * 设备状态
     */
    private String deviceStatus;

    /**
     * 设备状态
     */
    private String status;

    /**
     * 站址地址
     */
    private String location;

    /**
     * 父节点编码
     */
    private String parentCode;

    /**
     * 设备编码
     */
    private String code;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 当前节点等级
     */
    private String level;
    /**
     * 是否收藏 1:收藏 0：未收藏
     */
    private String isCollection;
    /**
     * 创建时间
     */
    private Date creatorDate;
    /**
     * 修改时间
     */
    private Date modifierDate;

    /**
     * 子节点列表
     */
    private List<IndustryDeviceTree> list;

    /**
     * 通道列表
     */
    private List<IndChannelInfoVO> channelInfoVOList;

    /** 事发地点经度 */
    private String alarmLongitude;
    /** 事发地点纬度 */
    private String alarmLatitude;

    /**
     * 订单编码
     */
    private String orderIds;
    /**
     * 设备类型:1-IPC 0-NVR;2-AR摄像头；3-热成像摄像头
     */
    private String categoryCode;

    /**
     * 联动动作 0:抓图 1:抓视频
     */
    private String linkageAction;

    /**
     * 图标地址
     */
    private String imageUrl;

    /**
     * 厂家编码
     */
    private String modelCode;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 设备厂家编码
     */
    private String deviceModelCode;

    /**
     * 街道编码
     */
    private String townCode;

    /**
     * 设备厂家名称
     */
    private String modelName;

    /**
     * 是否收藏字段，0未收藏 1已收藏
     */
    private String isMonitor;

    /**
     * 设备类型名称
     */
    private String deviceTypeName;


    /**
     * 雷达天线扫描周期 单位：秒
     */
    private String devicePeriod;

    /**
     * 雷达覆盖范围 单位：米
     */
    private String wZone;

    /**
     * 相控角度 单位:度
     */
    private String parAngle;

    /**
     * 无人机机巢编码
     */
    private String uavNestCode;

    /**
     * 无人机机巢状态
     */
    private String uavNestStatus;

    /**
     *  是否有未完结告警（0-否、1-是）
     */
    private String isCompleteFlag;

    /**
     * 设备父类型
     */
    private String deviceSubType;

    /**
     * 飞行半径
     */
    private Double flightRadius;

    /**
     * 是否机巢设备
     */
    private Boolean isNestDevice;

    /**
     * 机巢设备类型
     */
    private String uavNestPositionFix;

    /**
     * 机巢标定状态
     */
    private Integer uavNestCalibrationStatus;
}
