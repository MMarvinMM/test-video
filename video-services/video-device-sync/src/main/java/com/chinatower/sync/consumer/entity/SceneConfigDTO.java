package com.chinatower.sync.consumer.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 场景配置DTO
 */
@Data
public class SceneConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场景ID
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
}

