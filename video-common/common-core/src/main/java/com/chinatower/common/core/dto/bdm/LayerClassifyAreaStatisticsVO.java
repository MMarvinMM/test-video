package com.chinatower.common.core.dto.bdm;

import lombok.Data;

import java.util.List;

/**
 * @desription:
 * @data 创建时间：2024/5/31 13:51
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerClassifyAreaStatisticsVO {

    /**
     * 表名
     */
    private List<String> tableNames;
    /**
     * 统计的字段
     */
    private String code;
    /**
     * 面积字段
     */
    private String mj;
}
