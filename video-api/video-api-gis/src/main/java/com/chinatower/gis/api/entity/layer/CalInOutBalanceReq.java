package com.chinatower.gis.api.entity.layer;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalInOutBalanceReq extends CommonFeignEntity {
    /**
     * 之前的图层表名
     * */
    @ApiModelProperty(value = "之前的图层表名")
    private String preLayerName;
    /**
     * 当前的图层表名
     * */
    @ApiModelProperty(value = "当前的图层表名")
    private String curLayerName;
}