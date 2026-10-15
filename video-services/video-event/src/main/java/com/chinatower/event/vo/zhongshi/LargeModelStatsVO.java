package com.chinatower.event.vo.zhongshi;

import lombok.Data;

import java.util.List;

/**
 * 大模型统计
 */
@Data
public class LargeModelStatsVO {

    /**
     * 大模型父子表列表
     */
    private List<LargeModelParentStatsVO> modelList;
}
