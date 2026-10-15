package com.chinatower.algorithm.dataobject;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 场景告警关系表修改DTO
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysSceneAlarmUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场景ID（主键）
     */
    private String sceneId;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 告警类型列表
     */
    private List<AlarmTypeItem> alarmTypeList;

    /**
     * 算法编码
     */
    private String algorithmCode;
}

