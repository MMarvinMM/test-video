package com.chinatower.platform.entity;

import com.chinatower.event.entity.vo.AlgorithmEventVO;
import lombok.Data;

import java.util.List;

/**
 * 统计分析入参
 */
@Data
public class StatisticParam extends AlgorithmEventVO {
    /**
     * 开始时间
     */
    private String startDate;
    /**
     * 结束时间
     */
    private String endDate;
    /**
     * 告警类型批量查询
     */
    private List<String> warningTypeIdList;
    /**
     * 算法编码批量查询
     */
    private List<String> algorithmCodeList;
    /**
     * 当前页
     */
    private int pageNum = 1;
    /**
     * 条数限制
     */
    private int pageSize = 10;
}
