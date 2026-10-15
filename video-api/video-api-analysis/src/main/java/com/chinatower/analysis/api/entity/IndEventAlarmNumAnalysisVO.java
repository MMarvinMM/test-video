package com.chinatower.analysis.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * @Name IndEventAlarmNumVO
 * @Description
 * @Author Chensw
 * @Date 2022/04/2022/4/1 17 : 04
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndEventAlarmNumAnalysisVO {

    /**
     * 总告警数
     * @required true
     */
    @ApiModelProperty("总告警数")
    private Integer allNum;

    /**
     * AI告警数
     * @required true
     */
    @ApiModelProperty("AI告警数")
    private Integer AINum;

    /**
     * 人工告警数
     * @required true
     */
    @ApiModelProperty("人工告警数")
    private Integer manNum;

    /**
     * 一键告警数
     * @required true
     */
    @ApiModelProperty("一键告警数")
    private Integer keyNum;

    /**
     * 告警来源数量统计集合
     * @required true
     */
    private List<Map> sourceList;
}
