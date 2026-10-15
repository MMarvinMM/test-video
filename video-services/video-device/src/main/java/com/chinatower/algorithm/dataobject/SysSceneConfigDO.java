package com.chinatower.algorithm.dataobject;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 算法场景表
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Getter
@Setter
public class SysSceneConfigDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场景id 对应sys_alarm_type中的type_value字段（主键）
     */
    private String sceneId;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 告警类型 sys_alarm_type表的type_value
     */
    private String typeValue;

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
     * 修改时间
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

