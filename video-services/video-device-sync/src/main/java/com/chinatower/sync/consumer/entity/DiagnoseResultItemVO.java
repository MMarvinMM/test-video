package com.chinatower.sync.consumer.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DiagnoseResultItemVO implements Serializable {

    @ApiModelProperty(value = "告警阈值分数")
    private Double alarmScore;

    @ApiModelProperty(value = "算法编码，唯一标识检测算法")
    private String algorithmCode;

    @ApiModelProperty(value = "算法名称，说明检测类型")
    private String algorithmName;

    @ApiModelProperty(value = "检测结果（1 = 正常 / 无异常，2 = 异常）")
    private Integer detectResult;

    @ApiModelProperty(value = "检测分数，算法输出的量化结果")
    private Double detectScore;

    @ApiModelProperty(value = "检测资源地址 JSON 字符串，包含文件路径和预览地址")
    private String detectedImgUrl;

    @ApiModelProperty(value = "诊断方案子项 ID，关联具体算法配置")
    private Long diagnoseSchemeItemId;

    @ApiModelProperty(value = "检测时间，格式为 yyyy-MM-dd HH:mm:ss.SSS")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date diagnoseTime;
}
