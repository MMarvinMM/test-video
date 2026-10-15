package com.chinatower.analysis.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

/**
 * 入参VO
 */
@Data
public class QueryParamVO extends BaseDataScopeEntity {

    /**
     * 省份编码
     * @required true
     */
    private String provinceId;

    /**
     * 市编码
     * @required true
     */
    private String cityId;

    /**
     * 开始时间 YYYY-MM-DD
     * @required true
     */
    private String startDate;

    /**
     * 结束时间 YYYY-MM-DD
     * @required true
     */
    private String endDate;
}
