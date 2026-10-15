package com.chinatower.analysis.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 算法VO
 */
@Data
public class AlgorithmStatisticsVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 算法任务数量
     */
    private int totalNum;
    /**
     * 启用算法任务数量
     */
    private int enableNum;
}
