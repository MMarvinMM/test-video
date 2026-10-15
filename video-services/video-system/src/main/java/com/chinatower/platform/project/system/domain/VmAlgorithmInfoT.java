package com.chinatower.platform.project.system.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 算法基本信息表实体类（简化版）
 */
@Data
public class VmAlgorithmInfoT implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 算法ID */
    private Long algorithmId;

    /** 算法唯一编码 */
    private String algorithmCode;

    /** 算法小类编码（父告警类型编码） */
    private String algoMinTypeCode;

    /** 特殊参数说明（JSON格式） */
    private String modelConfig;

    /** 逻辑删除 0正常1删除 */
    private String deleteFlag;

    /** 1-有效；2-失效 */
    private String status;
}