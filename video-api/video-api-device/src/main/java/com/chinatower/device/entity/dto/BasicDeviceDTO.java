package com.chinatower.device.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by zheng.yan on 2022/1/21.
 */
@Data
public class BasicDeviceDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 设备归属平台编码
     */
    private String platformCode;
    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String devName;

    /**
     * 设备状态:0在线，1离线
     */
    private Integer status;

    /**
     * 设备厂家code
     */
    private String modelId;

    /**
     * 设备类型:1-IPC 0-NVR;2-AR摄像头；3-热成像摄像头
     */
    private Integer categoryCode;

    /**
     * 设备登录用户名
     */
    private String devUserName;

    /**
     * 设备登录密码
     */
    private String devPassword;

    /**
     * 设备扩展信息
     */
    private String devExInfo;

    /**
     * 国标编码
     */
    private String gb28181Code;

    /**
     * 设备名称缩写
     */
    private String devNameAbbr;

    /**
     * 字段说明
     */
    private String remark;

    /**
     * 是否具备抓拍功能：0-否；1-是
     */
    private Integer enableSnap;

    /**
     * 设备IP
     */
    private String ip;

    /**
     * 设备端口
     */
    private String port;

    /**
     * 设备状态:1-在建；2-在网；3-退网；4-闲置
     */
    private Integer enable;

    /**
     * 设备产权
     */
    private String owner;

    /**
     * 接入方式:0-国标；1-onvif；3-海康私有协议；4-大华私协；5-华为私协；6-宇视私协；
     * 7-浩海协议;8-科达协议;9-中林信达;10-中威; 11：海康ehome；12：大华主动注册；
     * 13：华为主动注册；14-友华通信；15-友华软件；
     */
    private Integer collectType;

    /**
     * 接入方式 0-ip接入；1-设备编号接入
     */
    private Integer collectMethod;

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
     * 归属组织
     */
    private String orgCode;

    /**
     * 操作类型 1-新增；2-修改；
     */
    private Integer optType;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    /**
     * 设备挂高，单位：米
     */
    private String height;

    /**
     * 所属管理区域
     */
    private String areaCode;

    /**
     * 行政区域
     */
    private String admCode;

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
     * 省份
     */
    private String provinceId;

    /**
     * 地市
     */
    private String cityId;

    /**
     * 区县
     */
    private String countyId;

    /**
     * 通道
     */
    private List<BasicDeviceChannelBo> channels;

    /**
     * 水平范围最小值
     */
    private String horizonMin;

    /**
     * 水平范围最大值
     */
    private String horizonMax;

    /**
     * 垂直范围从最小值
     */
    private String verticalMin;

    /**
     * 垂直范围最大值
     */
    private String verticalMax;

    /**
     * 视角场最小值
     */
    private String visualMin;

    /**
     * 视角场最大值
     */
    private String visualMax;

    /**
     * 接入节点
     */
    private String accessNode;

    /**
     * 行政区划名称
     */
    private String areaName;

    /**
     * 行政区划父节点编码
     */
    private String areaParentCode;
}
