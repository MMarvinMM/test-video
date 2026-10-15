package com.chinatower.industry.vo;

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
public class IndEventAlarmNumVO {

    @ApiModelProperty("总告警数")
    private Integer allNum;

    @ApiModelProperty("AI告警数")
    private Integer AINum;

    @ApiModelProperty("人工告警数")
    private Integer manNum;

    @ApiModelProperty("一键告警数")
    private Integer keyNum;

    private List<Map> sourceList;

}
