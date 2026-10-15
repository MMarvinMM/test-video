package com.chinatower.analysis.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Name IndAnalysisVO
 * @Description
 * @Author trk
 * @Date 2022/04/2022/4/7 20 : 36
 */
@Data
@ToString
@ApiModel(description = "重复报警信息查询入参")
public class IndAnalysisAnalysisVO extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 维度标识（1当年、2当月、3前一天、4当天）
     * @required true
     */
    private String dimensionFlag;

    /**
     * 告警开始时间
     * @required false
     */
    private String queryStartTime;

    /**
     * 告警结束时间
     * @required false
     */
    private String queryEndTime;

    /**
     * 省份编码
     * @required false
     */
    private String provinceId;

    /**
     * 地市编码
     * @required false
     */
    private String cityId;

    /**
     * 区县编码
     * @required false
     */
    private String countyId;
}
