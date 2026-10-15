package com.chinatower.device.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 物联设备表
 * </p>
 *
 * @author lyy
 * @since 2023-12-22
 */
@Data
public class IotTTDeviceVO extends IotDeviceVO implements Serializable {

    /**
     * 客户ID,客户名称
     */
    private String tenantIdOrName;
    /**
     * 省份 \省公司
     */
    private String provinceCompany;

    /**
     * 地市 \市公司
     */
    private String cityCompany;

    /**
     * 区县
     */
    private String countyCompany;


    private List<Long> deptProvinceCity;

    private Long chooseOrgId;

    /**
     * 客户数
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer custCount;

    /**
     * 客户名称、id 查询条件
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String custCode;


    /**
     * 属地化新增字段 - 运管--设备类型
     */
    private String deviceCategory;

    /**
     * 属地化-新增字段 物联设备接入协议
     */
    private String accessProtocols;

    /**
     * 属地化-新增字段 安装信息 共享属性
     */
    private String shareProperty;

    /**
     * 属地化-新增字段 安装信息 安装时间
     */
    private String installTime;

    /**
     * 属地化-新增字段 同步信息 同步状态 1成功/2未同步
     */
    private String syncStatus;

    /**
     * 属地化-新增字段 同步信息 同步时间
     */
    private Date syncTime;

    /**
     * 属地化-新增字段  重量
     */
    private String weight;

    /**
     * 属地化-新增字段 挂高类型
     */
    private String heightType;

    /**
     * 收藏设备类型
     */
    private String collObjType;

    /**
     * 属地化同步--接入节点
     */
    private String locationCode;

    /**
     * 接入节点名称
     */
    private String locationName;

    /**
     * 操作类型 1：同步
     */
    private Integer optType;

    /**
     * 组织编码-管理区域
     */
    private String deptId;


    /**
     * 属地化同步类型
     */
    private String opType;

    /**
     * 运管--设备类别
     */
    private String deviceBigType;

}
