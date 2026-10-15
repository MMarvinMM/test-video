package com.chinatower.algorithm.dataobject;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 场景告警关系表
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Getter
@Setter
public class SysSceneAlarmRelDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 场景id 对应sys_alarm_type中的type_value字段
     */
    private String sceneId;

    /**
     * 告警类型编码 对应vm_algorithm_info_t中的model_config
     */
    private String alarmType;

    /**
     * 告警类型名称 对应vm_algorithm_info_t中的model_config
     */
    private String alarmTypeName;

    /**
     * 算法编码
     */
    private String algorithmCode;

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

