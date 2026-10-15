package com.chinatower.algorithm.dataobject;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 大模型算法VO
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Getter
@Setter
public class ModelAlgorithmVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 算法编码
     */
    private String algorithmCode;

    /**
     * 算法名称
     */
    private String algorithmName;
}


