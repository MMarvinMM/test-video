package com.chinatower.algorithm.dataobject;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 是否包含大模型算法VO
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Getter
@Setter
public class HasModelAlgorithmVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否包含大模型算法
     */
    private Boolean hasModelAlgorithm;
}

