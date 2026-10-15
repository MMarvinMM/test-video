package com.chinatower.industry.vo;

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
public class IndEventNumVO {

    @ApiModelProperty("省编码")
    private String provinceId;

    @ApiModelProperty("市编码")
    private String cityId;

    @ApiModelProperty("区/县编码")
    private String countyId;

    @ApiModelProperty("行政区划地址")
    private String regionName;

    @ApiModelProperty("告警数量")
    private Integer num;

    /**
     * 真警数量
     * @required true
     */
    @ApiModelProperty("真警数量")
    private int realNum;

}
