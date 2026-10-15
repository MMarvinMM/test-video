package com.chinatower.common.core.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName OrderCustomerDO
 * @Deacription 订单客户表
 * @Author chenmf
 * @Date 2022/1/6 10:01
 * @Version 1.0
 **/
@Data
public class OrderCustomerDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    //订购关系编码
    @TableField("order_code")
    private String orderCode;

    //客户编码/租户id
    @TableField("tenant_id")
    private String tenantId;

    //客户名称
    @TableField("customer_name")
    private String customerName;

    //账号
    @TableField("admin")
    private String admin;

    //密码
    @TableField("password")
    private String password;

    //订购人账号
    @TableField("subscriber_code")
    private String subscriberCode;

    //订购人姓名
    @TableField("subscriber_name")
    private String subscriberName;

    //生效时间
    @TableField("valid_time")
    private String validTime;

    //失效时间
    @TableField("invalid_time")
    private String invalidTime;

    //业务归属组织
    @TableField("business_org")
    private String businessOrg;

    //订购时间
    @TableField("order_time")
    private String orderTime;

    //注册地所属省份
    @TableField("cus_registry_province")
    private String cusRegistryProvince;

    //注册地所属地市
    @TableField("cus_registry_city")
    private String cusRegistryCity;

    //所属区域
    @TableField("adm_area")
    private String admArea;

    //用户id
    @TableField("user_id")
    private String userId;

    //订单状态
    @TableField("ord_status")
    private Integer ordStatus;

    //删除标志位
    @TableField("delete_flag")
    private Integer deleteFlag;

    //行业编码
    @TableField("industry_code")
    private String industryCode;

    //行业版本编码
    @TableField("app_ver_code")
    private String appVerCode;

    //创建时间
    @TableField("create_time")
    private Date createTime;

    //更新时间
    @TableField("update_time")
    private Date updateTime;

    //迁移标志累加
    @TableField("move_sign")
    private Integer moveSign;

    /**
     * 受理节点
     */
    @TableField("accept_node")
    private String acceptNode;
}
