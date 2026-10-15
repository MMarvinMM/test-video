package com.chinatower.analysis.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ToString
public class CheckpointStatisticsConditionVO extends BaseDataScopeEntity {

    private static final long serialVersionUID = -7428691383220953249L;

    @ApiModelProperty(value = "维度标识")
    @NotNull(message = "维度标识不能为空")
    /**
     * 维度标识（1当年、2当月、3前一天、4当天）
     * @required true
     */
    private String dimensionFlag;

    private List<String> validDeviceCodes;

    /**
     * 开始时间
     * @required true
     */
    private String queryStartTime;

    /**
     * 结束时间
     * @required true
     */
    private String queryEndTime;

}
