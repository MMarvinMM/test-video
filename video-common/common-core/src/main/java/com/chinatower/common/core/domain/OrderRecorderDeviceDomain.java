package com.chinatower.common.core.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrderRecorderDeviceDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    //订购关系编码
    @TableField("order_code")
    private String orderCode;

    //客户编码/租户id
    @TableField("tenant_id")
    private String tenantId;

    //套餐编码
    @TableField("package_code")
    private String packageCode;

    //设备编码
    @TableField("device_code")
    private String deviceCode;

    //设备名称
    @TableField("device_name")
    private String deviceName;

    //设备状态
    @TableField("status")
    private int status;

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
