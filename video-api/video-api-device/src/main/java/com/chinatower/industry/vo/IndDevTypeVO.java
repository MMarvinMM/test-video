package com.chinatower.industry.vo;

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
public class IndDevTypeVO {

    @ApiModelProperty("告警类型编码")
    private String typeId;

    @ApiModelProperty("告警类型")
    private String typeName;

    @ApiModelProperty("告警总数")
    private Integer num;

    @ApiModelProperty("AI告警总数")
    private Integer aiNum;

    @ApiModelProperty("研判告警总数")
    private Integer judgeNum;

    @ApiModelProperty("真告警总数")
    private Integer realNum;

    @ApiModelProperty("假告警总数")
    private Integer falseNum;



}
