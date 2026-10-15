package com.chinatower.platform.project.custom.entity;


import com.chinatower.common.annotation.Excel;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;


@Data
public class TenantMangeResultVO extends BaseDataScopeEntity {
    String userId;

    String alarmLocation;

    String province_id;

    @Excel(name = "省份")
    String provinceName;

    String city_id;

    @Excel(name = "地市")
    String cityName;

    @Excel(name = "客户名称")
    String customerName;

    @Excel(name = "客户ID")
    String customerCode;

    @Excel(name = "客户账号")
    String tenantManager;

    @Excel(name = "订单总数")
    String orderNum;

    @Excel(name = "有效订单数")
    String validOrderNum;

    @Excel(name = "有效设备数")
    String devNum;

    String customerStatus;

    @Excel(name = "客户状态")
    String customerStatusName;

    String isOpen;

    @Excel(name = "开放API状态")
    String isOpenName;

    @Excel(name = "API操作人")
    String openModifier;

    @Excel(name = "API操作时间")
    String openModifierDate;

    String isOperation;

    @Excel(name = "运营研判")
    String isOperationName;

    @Excel(name = "运营研判操作人")
    String operationModifier;

    private String operationModifierName;

    @Excel(name = "运营研判操作时间")
    String operationModifierDate;

    String apiCreateBy;

    private String apiCreateName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    String apiCreateTime;

    String operationAlarmTypeStr;

    /**
     * 级联状态中文描述
     */
    String cascadeStatus;

    /**
     * 级联状态
     */
    String isCascade;

    String cascadeModifier;

    String cascadeModifierName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    String cascadeModifierDate;

    String platCode;

    String permissionType;

    String deviceCode;

    private List<TenantLocationRelVO> tenantLocationRelVOList;


    /**
     * 节点编号
     */
    private String locationCode;

    /**
     * 节点名称
     */
    private String locationName;

    /**
     * 是否关联
     */
    private Integer isRel;


    /**
     * 登录类型（0：默认类型 1：自定义）
     */
    private String loginType;

    /**
     * 登陆地址
     */
    private String loginUrl;
}
