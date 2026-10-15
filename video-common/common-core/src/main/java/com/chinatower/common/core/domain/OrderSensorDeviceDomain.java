package com.chinatower.common.core.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName OrderSensorDeviceDO
 * @Deacription 订单传感器设备通道表
 * @Author chenmf
 * @Date 2022/1/6 10:54
 * @Version 1.0
 **/
@Data
public class OrderSensorDeviceDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    //订购关系编码
    @TableField("order_code")
    private String orderCode;

    //客户编码/租户id
    @TableField("tenant_id")
    private String tenantId;

    //设备类型 1-雷达,2传感器
    @TableField("category_code")
    private int categoryCode;

    //套餐编码
    @TableField("package_code")
    private String packageCode;

    //设备编码
    @TableField("device_code")
    private String deviceCode;

    //设备名称
    @TableField("device_name")
    private String deviceName;

    //站址编码
    @TableField("site_code")
    private String siteCode;

    //站址名称
    @TableField("site_name")
    private String siteName;

    //设备状态
    @TableField("status")
    private int status;

    //公共库站址编码
    @TableField("public_site_code")
    private String publicSiteCode;

    //归属区域
    @TableField("area_code")
    private String areaCode;

    //归属区域
    @TableField("adm_code")
    private String admCode;

    //站址地址
    @TableField("location")
    private String location;

    //经度
    @TableField("longitude")
    private String longitude;
    //纬度
    @TableField("latitude")
    private String latitude;

    //高度
    @TableField("height")
    private String height;

    //删除标志位
    @TableField("delete_flag")
    private Integer deleteFlag;

    //创建时间
    @TableField("create_time")
    private Date createTime;

    //更新时间
    @TableField("update_time")
    private Date updateTime;

    //迁移标志累加
    @TableField("move_sign")
    private Integer moveSign;

    //状态
    @TableField("ord_status")
    private Integer ordStatus;

    //行业编码
    @TableField("industry_code")
    private String industryCode;

    //行业版本编码
    @TableField("app_ver_code")
    private String appVerCode;

    /**
     * 受理节点
     */
    @TableField("accept_node")
    private String acceptNode;

    /**
     * 接入节点
     */
    @TableField("access_node")
    private String accessNode;
}
