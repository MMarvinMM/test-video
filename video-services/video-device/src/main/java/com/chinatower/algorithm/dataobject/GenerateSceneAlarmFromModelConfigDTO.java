package com.chinatower.algorithm.dataobject;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 根据 model_config 生成场景告警配置的请求DTO
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Getter
@Setter
public class GenerateSceneAlarmFromModelConfigDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 算法编码
     */
    private String algorithmCode;
}

