package com.chinatower.device.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 物联设备表
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-07
 */
@Getter
@Setter
@TableName("vm_iot_device_t")
public class IotDeviceDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "iot_device_id", type = IdType.AUTO)
    private Long iotDeviceId;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

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
     * 设备类型
     */
    private Integer categoryCode;

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
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 0正常1删除
     */
    private String deleteFlag;

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

    /**
     * 乡镇
     */
    private String townCode;
}
