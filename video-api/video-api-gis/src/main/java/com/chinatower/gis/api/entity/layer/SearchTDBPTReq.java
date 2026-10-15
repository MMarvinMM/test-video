package com.chinatower.gis.api.entity.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchTDBPTReq extends GetFeaturesByFilterReqEx {
    /**
     * 项目名称
     * @required false
     * */
    @ApiModelProperty(value = "项目名称")
    private String xmmc;

    /**
     * 批准文号
     * @required false
     * */
    @ApiModelProperty(value = "批注文号")
    private String pzwh;
}
