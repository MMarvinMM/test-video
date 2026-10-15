package com.chinatower.sync.consumer.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class VodResultVO implements Serializable {

    @ApiModelProperty(value = "通道编码")
    private String channelCode;
    @ApiModelProperty(value = "通道名称")
    private String channelName;
    @ApiModelProperty(value = "设备编码")
    private String deviceCode;
    @ApiModelProperty(value = "设备名称")
    private String deviceName;
    @ApiModelProperty(value = "诊断计划 ID，关联对应的诊断任务配置")
    private Long diagnosePlanId;
    @ApiModelProperty(value = "诊断结果状态（0：未诊断；1：正常；2：异常，其他值需结合业务扩展）")
    private Integer diagnoseResult;
    @ApiModelProperty(value = "诊断结果描述，说明核心异常类型")
    private String diagnoseResultDetail;
    @ApiModelProperty(value = "具体检测项列表，包含各算法的检测结果")
    private List<DiagnoseResultItemVO> diagnoseResultItems;
    @ApiModelProperty(value = "诊断方案 ID，关联诊断算法组合配置")
    private Long diagnoseSchemeId;
    @ApiModelProperty(value = "诊断时间，格式为 yyyy-MM-dd HH:mm:ss.SSS")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date diagnoseTime;
    @ApiModelProperty(value = "数据过期天数，默认 365 天")
    private Integer expireDays;
    @ApiModelProperty(value = "全局唯一标识，用于关联同一诊断任务的相关数据")
    private String gid;
    @ApiModelProperty(value = "索引标识")
    private Integer idx;
    @ApiModelProperty(value = "资源地址 JSON 字符串，包含文件路径和预览地址")
    private String imgUrl;
    @ApiModelProperty(value = "设备 IP 地址")
    private String ip;
    @ApiModelProperty(value = "组织编码，设备所属组织唯一标识")
    private String orgCode;
    @ApiModelProperty(value = "组织 ID，设备所属组织编号")
    private String orgId;
    @ApiModelProperty(value = "组织名称，设备所属组织名称")
    private String orgName;
    @ApiModelProperty(value = "设备厂商名称")
    private String producerName;
    @ApiModelProperty(value = "租户 ID，多租户环境下的租户唯一标识")
    private String tenantId;

}
