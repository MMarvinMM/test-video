package com.chinatower.event.vo.zhongshi;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 大模型下游算法子表统计
 */
@Data
public class LargeModelChildStatsVO {

    /**
     * 下游算法名称
     */
    private String downstreamAlgorithmName;

    /**
     * 首次通过率
     */
    private BigDecimal firstPassRate;

    /**
     * 最终通过率
     */
    private BigDecimal finalPassRate;
}
