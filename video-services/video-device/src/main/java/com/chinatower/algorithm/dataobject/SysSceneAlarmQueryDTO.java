package com.chinatower.algorithm.dataobject;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 场景告警关系表查询DTO
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Getter
@Setter
public class SysSceneAlarmQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场景id
     */
    private String sceneId;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 告警类型编码列表
     */
    private List<String> alarmTypeList;

    /**
     * 告警类型名称
     */
    private String alarmTypeName;

    /**
     * 算法编码
     */
    private String algorithmCode;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 租户ID（内部使用，自动填充）
     */
    private String tenantId;

    /**
     * 行业编码（内部使用，自动填充）
     */
    private String industryCode;

    /**
     * 应用版本编码（内部使用，自动填充）
     */
    private String appVerCode;
}

