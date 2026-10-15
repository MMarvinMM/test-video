package com.chinatower.analysis.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Name IndDevTypeVO
 * @Description
 * @Author Chensw
 * @Date 2022/04/2022/4/1 16 : 57
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndDevTypeAnalysisVO {

    /**
     * 告警类型编码
     * @required true
     */
    @ApiModelProperty("告警类型编码")
    private String typeId;

    /**
     * 告警类型
     * @required true
     */
    @ApiModelProperty("告警类型")
    private String typeName;

    /**
     * 告警总数
     * @required true
     */
    @ApiModelProperty("告警总数")
    private Integer num;

    /**
     * AI告警总数
     * @required true
     */
    @ApiModelProperty("AI告警总数")
    private Integer aiNum;

    /**
     * 研判告警总数
     * @required true
     */
    @ApiModelProperty("研判告警总数")
    private Integer judgeNum;

    /**
     * 真告警总数
     * @required true
     */
    @ApiModelProperty("真告警总数")
    private Integer realNum;

    /**
     * 假告警总数
     * @required true
     */
    @ApiModelProperty("假告警总数")
    private Integer falseNum;



}
