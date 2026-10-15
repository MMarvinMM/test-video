package com.chinatower.receive.vo.entity;

import com.chinatower.receive.core.intercept.SuppressionStrategy;
import lombok.Data;

@Data
/**
 * 告警压制策略排序类
 */
public class OrderedStrategy{

    private final SuppressionStrategy strategy;
    private final int order;

    public OrderedStrategy(SuppressionStrategy strategy, int order) {
        this.strategy = strategy;
        this.order = order;
    }

}
