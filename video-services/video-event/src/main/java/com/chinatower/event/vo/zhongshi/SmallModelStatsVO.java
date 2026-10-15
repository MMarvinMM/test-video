package com.chinatower.event.vo.zhongshi;

import lombok.Data;

/**
 * 小模型统计
 */
@Data
public class SmallModelStatsVO {

    /**
     * 已完成算法
     */
    private SmallModelTabStatsVO completed;

    /**
     * 进行中算法
     */
    private SmallModelTabStatsVO inProgress;
}
