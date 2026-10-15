package com.chinatower.order.dto;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection="sys_tenant_info_his")
public class SysTenantInfoHisMongoDTO  extends BaseMongoDTO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 租户描述
     */
    private String tenantDesc;

    /**
     * 行业信息
     */
    private String tradeInfo;

    /**
     * 删除标志（0未删除 1已删除）
     */
    private Integer delFlag;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 省份id
     */
    private String provinceId;

    /**
     * 地市id
     */
    private String cityId;

    /**
     * 部门id
     */
    private String deptId;

    /**
     * 区县id
     */
    private String countyId;

    /**
     * 运营研判状态：0:关闭 1:开启
     */
    private String isOperation;

    /**
     * 级联开启状态：0:关闭 1:开启
     */
    private String isCascade;

    /**
     * 告警定位（1:开启 0关闭）
     */
    private String alarmLocation;

    /**
     * 租户是否有有效订单 1：是 0：否
     */
    private String status;

    /**
     * 【废弃】运营研判模式关联告警类型串(空:全选)
     */
    private String operationAlarmTypeStr;

    /**
     * 运营研判操作人
     */
    private String operationModifier;

    /**
     * 运营研判操作时间
     */
    private Date operationModifierDate;

    /**
     * 级联操作人
     */
    private String cascadeModifier;

    /**
     * 级联操作人名称
     */
    private String cascadeModifierName;

    /**
     * 级联操作时间
     */
    private Date cascadeModifierDate;

    /**
     * 登录类型（0：默认类型 1：自定义）
     */
    private String loginType;

    /**
     * 登录地址/链接
     */
    private String loginUrl;
}
