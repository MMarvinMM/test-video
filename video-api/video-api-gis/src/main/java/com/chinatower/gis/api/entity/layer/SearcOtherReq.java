package com.chinatower.gis.api.entity.layer;

import com.chinatower.gis.api.entity.Constants;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearcOtherReq extends GetFeaturesByFilterReqEx {
    /**
    * 标识码
    * @required false
    */
    @ApiModelProperty(value = "标识码")
    @NotBlank(message = Constants.NOT_EMPTY_PARAM)
    private String bsm;

    /**
     * 要素代码
     * @required false
     * */
    @ApiModelProperty(value = "要素代码")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private String ysdm;
    /**
     * 行政区代码
     * @required false
     * */
    @ApiModelProperty(value = "行政区代码")
    @NotBlank(message = Constants.NOT_EMPTY_MAP_TYPE)
    private String xzqdm;
}
