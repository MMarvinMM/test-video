package com.chinatower.sync.consumer.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 场景告警关系DTO
 */
@Data
public class SceneAlarmRelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场景ID
     */
    private String sceneId;

    /**
     * 告警类型编码
     */
    private String alarmType;

    /**
     * 告警类型名称
     */
    private String alarmTypeName;
}

