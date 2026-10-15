package com.chinatower.analysis.api.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Name IndEventTrendDTO
 * @Description
 * @Author Chensw
 * @Date 2022/04/2022/4/1 14 : 46
 */
@Data
public class IndEventTrendAnalysisDTO extends BaseDataScopeEntity {

    /**
     * 趋势标识（year 年、month 月、day 天）
     * @required true
     */
    @ApiModelProperty(value = "趋势标识", notes = "year 年、month 月、day 天")
    private String trendFlag;

    /**
     * 搜索时间开始YYYY-MM-DD 都传才会生效
     * @required true
     */
    private String queryStartTime;

    /**
     * 搜索时间结束YYYY-MM-DD 都传才会生效
     * @required true
     */
    private String queryEndTime;

    /**
     * 省编码
     * @required false
     */
    private String provinceId;

    /**
     * 市编码
     * @required false
     */
    private String cityId;

    /**
     * 区县编码
     * @required false
     */
    private String countyId;

}
