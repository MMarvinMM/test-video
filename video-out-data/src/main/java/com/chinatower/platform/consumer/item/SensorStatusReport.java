package com.chinatower.platform.consumer.item;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

/**
 * 传感器状态数据对象
 * Created by zhujun on 2022/3/31.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SensorStatusReport extends BaseMongoDTO {
    // 设备编码
    private String deviceCode;
    // 更新时间 格式yyyy-MM-dd HH:mm:ss
    private String updateTime;
    // 在线状态 在线状态: 0在线 1离线
    private int onlineStatus;
    //设备名称
    private String deviceName;
    // 设备类型，运管录入，各系统与运管同步
    private String catalogType;
    //省份
    private String provinceId;
    //地市
    private String cityId;
    //区县
    private String countyId;
    //设备类型
    private Integer deviceType;
    //备注
    private String remark;
    //业务系统站址编码
    private String siteCode;
    //业务系统站址名称
    private String siteName;
    //公共库站址编码
    private String publicSiteCode;
    //设备厂家code
    private String manufacturer;
    //设备挂高，单位：米
    private String hangingHeight;
    //站址地址
    private String address;
    //经度
    private String longitude;
    //纬度
    private String latitude;
    //所属管理区域
    private String manageArea;
    //行政区域
    private String adminArea;
    // 组织编码
    private String dept;
    //物联设备型号编码
    private String deviceModel;
    //接入协议
    private String accessProtocols;
    //应用修改标识（1:已修改 0未修改）
    private String appModFlag;
    //乡镇编码
    private String townCode;
    //乡镇
    private String townName;
    //创建人
    private String createBy;
    //修改人
    private String updateBy;
    //创建时间
    private Date createTime;
    //删除标识
    private String deleteFlag;
    // 雷达天线扫描周期 单位：秒
    private String devicePeriod;
    // 雷达覆盖范围 单位：海里
    private String wZone;
    // 相控角度
    private String parAngle;
    // 属地化同步新增--接入节点
    private String locationCode;

    private String accessNode;

    private String acceptNode;
    /**
     * 删除状态，0：不删除；1：删除
     */
    private String delFlag;

    // canal类型
    private String canalType;
    // 执行时间
    private long ts;
}
