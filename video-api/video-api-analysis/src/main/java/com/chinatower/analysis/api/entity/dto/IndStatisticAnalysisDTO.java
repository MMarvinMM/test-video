package com.chinatower.analysis.api.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Name IndStatisticDTO
 * @Description
 * @Author Chensw
 * @Date 2022/04/2022/4/1 11 : 02
 */
@Data
public class IndStatisticAnalysisDTO extends BaseDataScopeEntity {

    /**
     * 维度标识（1当年、2当月、3前一天、4当天）
     * @required true
     */
    @ApiModelProperty(value = "维度标识", notes = "1当年、2当月、3前一天、4当天")
    private Integer dimensionFlag;

    /**
     * 开始时间，当dimensionFlag为5的时候传（YYYY-MM-DD）
     * @required true
     */
    private String queryStartTime;

    /**
     * 结束时间，当dimensionFlag为5的时候传（YYYY-MM-DD）
     * @required true
     */
    private String queryEndTime;
    //省id
    private String provinceId;

    //市id
    private String cityId;

    //区县id
    private String countyId;
}
