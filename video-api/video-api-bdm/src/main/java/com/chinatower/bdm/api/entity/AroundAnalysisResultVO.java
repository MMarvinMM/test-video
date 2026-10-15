package com.chinatower.bdm.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 周边分析结果vo
 * @author liuxt
 */
@Data
@ToString
public class AroundAnalysisResultVO  implements Serializable {
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("位置")
    private String location;
    @ApiModelProperty("距离（km）")
    private String distance;
    @ApiModelProperty("经度")
    private String longitude;
    @ApiModelProperty("纬度")
    private String latitude;
    @ApiModelProperty("类型")
    private String type;
    @ApiModelProperty("图片地址")
    private String icon;
    @ApiModelProperty("设备标识")
    private String id;
    @ApiModelProperty("设备状态")
    private String status;

    @ApiModelProperty("自定义图标")
    private String imageUrl;

    @ApiModelProperty("是否有未完结告警（0-否、1-是）")
    private String isCompleteFlag;

    @ApiModelProperty("重要等级")
    private String importanceLevel;

    @ApiModelProperty("重要等级名称")
    private String importanceLevelName;

    @ApiModelProperty("接入节点")
    private String accessNode;

    @ApiModelProperty("无人机机巢编码")
    private String uavNestCode;

    @ApiModelProperty("无人机机巢名称")
    private String uavNestName;

    @ApiModelProperty("无人机机巢状态")
    private String uavNestStatus;

    @ApiModelProperty("无人机飞行状态")
    private String flyStatus;
}
