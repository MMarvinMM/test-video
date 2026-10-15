package com.chinatower.event.dto.zhongshi;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 中试统计查询时间参数
 */
@Data
public class ZhongShiDateRangeDTO {

    /**
     * 自定义开始日期（yyyy-MM-dd）
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    /**
     * 自定义结束日期（yyyy-MM-dd）
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
}
