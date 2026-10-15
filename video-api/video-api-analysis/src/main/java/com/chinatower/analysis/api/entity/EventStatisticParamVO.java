package com.chinatower.analysis.api.entity;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EventStatisticParamVO extends CommonFeignEntity {

    /**
     * 开始时间
     * @required true
     */
    @NotBlank(message = "开始时间不能为空")
    private String startTime;

    /**
     * 结束时间
     * @required true
     */
    @NotBlank(message = "结束时间不能为空")
    private String endTime;

    /**
     * 区域编码
     * @required false
     */
    private String areaCode;

    /**
     * 统计维度：TYPE=告警类型（默认）；SUB_TASK=下游任务
     */
    private String statDimension;
}
