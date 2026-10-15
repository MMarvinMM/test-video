package com.chinatower.algorithm.dataobject;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 告警类型表DO
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Getter
@Setter
public class SysAlarmTypeDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 告警类型ID
     */
    private Long alarmTypeId;

    /**
     * 类型分类表ID
     */
    private String alarmTypeClassId;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 告警类型编码
     */
    private String typeValue;

    /**
     * 告警类型编码-弃用
     */
    private String typeCode;

    /**
     * 告警来源：1、AI告警 2、人工上报 3、一键告警 4.摄像机类型 5、车辆卡口 6、人脸卡口 8、物联设备告警 9、卫星告警 10、雷达告警
     */
    private String alarmSource;

    /**
     * 租户
     */
    private String tenantor;

    /**
     * 来源: 0-初始化;1-操作;2-铁塔新增
     */
    private String source;

    /**
     * 是否赠送 0:否  1:是
     */
    private String ifFree;

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
     * 更新时间
     */
    private Date updateTime;

    /**
     * 0正常 1删除
     */
    private Integer deleteFlag;

    /**
     * 图标
     */
    private String icon;

    /**
     * 行业应用版本
     */
    private String appVerCode;

    /**
     * 客户ID
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;
}

