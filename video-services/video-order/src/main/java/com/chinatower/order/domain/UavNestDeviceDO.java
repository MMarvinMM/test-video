package com.chinatower.order.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("vm_uav_nest_device_info")
public class UavNestDeviceDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
     * 设备类型，运管录入，各系统与运管同步
     */
    private String deviceType;

    /**
     * 设备状态:0在线，1离线
     */
    private Integer resourceStatus;

    /**
     * 省份
     */
    private String provinceCode;

    /**
     * 地市
     */
    private String cityCode;

    /**
     * 区县
     */
    private String countyCode;

    /**
     * 设备厂家code
     */
    private String modelCode;

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
     * 设备厂家编码
     */
    private String manufacturerCode;

    /**
     * 应用修改标识（1:已修改 0未修改）
     */
    private String appModFlag;

    /**
     * 乡镇
     */
    private String townCode;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 创建时间
     */
    private Date createTime;


    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 0正常1删除
     */
    private Integer deleteFlag;
}
