package com.chinatower.event.vo.zhongshi;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 小模型 TAB 统计
 */
@Data
public class SmallModelTabStatsVO {

    /**
     * 算法总数
     */
    private Integer algorithmTotal;

    /**
     * 首次通过率
     */
    private BigDecimal firstPassRate;

    /**
     * 最终通过率
     */
    private BigDecimal finalPassRate;

    /**
     * 算法名称明细
     */
    private List<String> algorithmNames;
}
