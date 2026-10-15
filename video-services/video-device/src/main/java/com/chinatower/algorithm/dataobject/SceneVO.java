package com.chinatower.algorithm.dataobject;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 场景VO
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Getter
@Setter
public class SceneVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场景id
     */
    private String sceneId;

    /**
     * 场景名称
     */
    private String sceneName;
}

