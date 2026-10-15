package com.chinatower.common.core.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName OrderPackageDO
 * @Deacription 订单套餐表
 * @Author chenmf
 * @Date 2022/1/6 11:09
 * @Version 1.0
 **/

@Data
public class OrderPackageDomain implements Serializable {

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

    //套餐名称
    @TableField("package_name")
    private String packageName;

    //套餐描述
    @TableField("description")
    private String description;

    //行业编码
    @TableField("industry_code")
    private String industryCode;

    //行业版本编码
    @TableField("app_ver_code")
    private String appVerCode;

    //应用版本
    @TableField("app_ver_name")
    private String appVerName;

    //应用能力
    @TableField("capabilities_str")
    private String capabilitiesStr;

    //存储时长
    @TableField("storage_time")
    private String storageTime;

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

    /**
     * 受理节点
     */
    @TableField("accept_node")
    private String acceptNode;
}
