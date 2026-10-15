package com.chinatower.sync.consumer.item;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class UavModelReport extends CommonYgReport implements Serializable {

    @ApiModelProperty(value = "型号ID")
    private String dictCode;

    @ApiModelProperty(value = "基站型号")
    private String dictLabel;

    @ApiModelProperty(value = "运管id")
    private String id;

    @ApiModelProperty(value = "生产厂家")
    private String manufacturer;

    @ApiModelProperty(value = "厂商编码")
    private String manufacturerCode;

    @ApiModelProperty(value = "负载枚举值")
    private String domain;

    private String type;

    private String subType;

    @ApiModelProperty(value = "飞行器类别")
    private String aircraftCategory;

    @ApiModelProperty(value = "飞行器类型")
    private String aircraftType;

    @ApiModelProperty(value = "'起飞全重（kg)")
    private String weight;

    @ApiModelProperty(value = "支持相机")
    private String supportCamera;

    @ApiModelProperty(value = "最大飞行时间（min）")
    private String maxFlyTime;

    @ApiModelProperty(value = "最大水平速度（m/s）")
    private String maxLevelSpeed;

    @ApiModelProperty(value = "搭配机巢")
    private String matchNest;
}