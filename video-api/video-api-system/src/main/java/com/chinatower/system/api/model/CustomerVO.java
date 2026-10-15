package com.chinatower.system.api.model;

import lombok.Data;
import lombok.ToString;

/**
 * 客户信息
 */
@Data
@ToString
public class CustomerVO {
    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户管理员账号
     */
    private String admin;

    /**
     * 客户管理员密码（BASE64）
     */
    private String password;

    /**
     * 订购关系编码
     */
    private String orderCode;

    /**
     * 生效时间
     */
    private String validTime;

    /**
     * 失效时间
     */
    private String invalidTime;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 所属行政区划
     */
    private String admArea;

    /**
     * 订购人姓名
     */
    private String subscriber;

    /**
     * 订购人账号
     */
    private String subscriberAccount;

    /**
     * 订购设备（查询订购设备的算法列表用）
     */
    private String deviceCode;

    /**
     * 业务归属组织
     */
    private String businessOrg;
    /**
     * 订购时间
     */
    private String orderTime;
    /**
     * 注册地所属省份
     */
    private String cusRegistryProvince;
    /**
     * 注册地所属地市
     */
    private String cusRegistryCity;
    /**
     * 订单状态：0变更;1新增订购;2订单续订、启用;3退订;4停用、失效
     */
    private Integer ordStatus;
    /**
     * 状态变化过程（1-2-3）
     */
    private String statusChangeProcess;

    /**
     * 0:失效 1:有效
     */
    private String deleteFlag;

    /**
     * 算法编号
     */
    private String algorithmCode;

    /**
     * 电话数量
     */
    private String phoneNum;
}