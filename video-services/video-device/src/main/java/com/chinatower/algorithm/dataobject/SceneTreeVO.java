package com.chinatower.algorithm.dataobject;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 场景树状结构VO
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SceneTreeVO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 场景id（字符串）
     */
    private String sceneId;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 算法编码
     */
    private String algorithmCode;

    /**
     * 告警类型列表（子节点）
     */
    private List<AlarmTypeVO> children;

    /**
     * 是否支持绊线
     */
    private boolean supportTripThread;
}

