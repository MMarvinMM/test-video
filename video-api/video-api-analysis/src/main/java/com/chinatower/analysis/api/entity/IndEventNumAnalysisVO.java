package com.chinatower.analysis.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Name IndEventNumAreaVO
 * @Description
 * @Author Chensw
 * @Date 2022/04/2022/4/1 17 : 13
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndEventNumAnalysisVO {

    /**
     * 省编码
     * @required true
     */
    @ApiModelProperty("省编码")
    private String provinceId;

    /**
     * 市编码
     * @required true
     */
    @ApiModelProperty("市编码")
    private String cityId;

    /**
     * 区/县编码
     * @required true
     */
    @ApiModelProperty("区/县编码")
    private String countyId;

    /**
     * 行政区划地址
     * @required true
     */
    @ApiModelProperty("行政区划地址")
    private String regionName;

    /**
     * 告警数量
     * @required true
     */
    @ApiModelProperty("告警数量")
    private Integer num;

    /**
     * 真警数量
     * @required true
     */
    @ApiModelProperty("真警数量")
    private int realNum;

}
