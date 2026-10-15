package com.chinatower.receive.model;

import lombok.Data;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/12/20 10:46
 */

@Data
public class TriggerRuleVo {

    /**
     * 关系
     */
    private String relation;
    /**
     * 规则
     */
    private List<RangeRule> rangeRule;

    @Data
    public static class RangeRule{
        /**
         * 最小值
         */
        private Rule min;
        /**
         * 最大值
         */
        private Rule max;
        /**
         * 字段命
         */
        private String field;
    }
    @Data
    public static class Rule{
        private String value;
        private String symbol;
    }
}
