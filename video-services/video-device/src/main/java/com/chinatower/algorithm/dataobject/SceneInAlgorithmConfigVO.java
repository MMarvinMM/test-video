package com.chinatower.algorithm.dataobject;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 场景是否在算法配置中VO
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Getter
@Setter
public class SceneInAlgorithmConfigVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场景ID
     */
    private String sceneId;

    /**
     * 是否在算法配置中
     */
    private Boolean inAlgorithmConfig;
}


