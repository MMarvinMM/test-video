package com.chinatower.platform.entity.device;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName DeviceInfoDTO
 * @Description 设备请求实体类
 * @Author chensw
 * @Date 2022/1/18
 */
@Data
public class SaveOrUpdateDeviceDTO implements Serializable {
    
    /**
     * 设备编码
     */
    private String deviceCode;
    
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 接入节点
     */
    private String accessNode;
    
    /**
     * 设备状态:0在线，1离线
     */
    private Integer deviceStatus;

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
     * 设备类型:1-IPC 0-NVR;2-AR摄像头；3-热成像摄像头
     */
    private Integer categoryCode;
    
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
     * 设备归属平台编码
     */
    private String platFormCode;
    
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
     * 行政区域
     */
    private String townCode;

}
