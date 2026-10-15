package com.chinatower.device.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskContentWithStrategyInfo {

    @ApiModelProperty(value = "内容名称")
    private String contentName;

    @ApiModelProperty(value = "单位:byte")
    private Integer contentSize;

    @ApiModelProperty(value = "内容类型 AUDIO、TEXT")
    private String contentType;

    @ApiModelProperty(value = "内容资源Md5值")
    private String md5;

    @ApiModelProperty(value = "文件地址")
    private String resourceUrl;

    @ApiModelProperty(value = "内容策略")
    private TaskContentStrategyInfo taskContentStrategyInfo;

    @ApiModelProperty(value = "内容时长")
    private String trackLength;
}