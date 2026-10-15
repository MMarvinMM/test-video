package com.chinatower.algorithm.dataobject;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 场景是否在算法配置中查询DTO
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Getter
@Setter
public class SceneInAlgorithmConfigQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场景ID
     */
    private String sceneId;
}


