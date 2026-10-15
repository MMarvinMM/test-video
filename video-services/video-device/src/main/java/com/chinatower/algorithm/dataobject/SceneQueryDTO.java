package com.chinatower.algorithm.dataobject;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 场景查询请求DTO
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Getter
@Setter
public class SceneQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场景名称（可选）
     */
    private String sceneName;

    private String algorithmCode;
}

