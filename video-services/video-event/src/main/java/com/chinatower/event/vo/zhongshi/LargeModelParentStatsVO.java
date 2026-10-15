package com.chinatower.event.vo.zhongshi;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 大模型父表统计
 */
@Data
public class LargeModelParentStatsVO {

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 下游算法数量
     */
    private Integer downstreamAlgorithmCount;

    /**
     * 首次通过率
     */
    private BigDecimal firstPassRate;

    /**
     * 最终通过率
     */
    private BigDecimal finalPassRate;

    /**
     * 子表
     */
    private List<LargeModelChildStatsVO> children;
}
