package com.chinatower.industry.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Name IndEventTrendVO
 * @Description
 * @Author Chensw
 * @Date 2022/04/2022/4/1 17 : 18
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndEventTrendVO {

    @ApiModelProperty("标签")
    private String label;

    @ApiModelProperty("告警数量")
    private Integer num;

    /**
     * 真警数量
     * @required true
     */
    @ApiModelProperty("真警数量")
    private int realNum;

}
