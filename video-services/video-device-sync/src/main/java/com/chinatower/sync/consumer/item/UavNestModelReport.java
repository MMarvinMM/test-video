package com.chinatower.sync.consumer.item;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class UavNestModelReport extends CommonYgReport implements Serializable {

    @ApiModelProperty(value = "型号ID")
    private String dictCode;

    @ApiModelProperty(value = "基站型号")
    private String dictLabel;

    @ApiModelProperty(value = "运管id")
    private String id;

    @ApiModelProperty(value = "生产厂家")
    private String manufacturer;

    @ApiModelProperty(value = "厂商code")
    private String manufacturerCode;

    @ApiModelProperty(value = "巡航半径")
    private String radius;

}
