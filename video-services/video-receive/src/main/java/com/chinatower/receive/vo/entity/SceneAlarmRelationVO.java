package com.chinatower.receive.vo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 场景告警关系查询结果VO
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Data
public class  SceneAlarmRelationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 场景编码（scene_id）
     */
    private String sceneCode;

    /**
     * 告警子项编码（alarm_type）
     */
    private String alarmType;

    private String algorithmCode;

    /**
     * 算法告警编码
     */
    private String algorithmAlarmType;

    /**
     * 高参
     */
    private String modelConfig;

}

