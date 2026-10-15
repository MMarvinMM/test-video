package com.chinatower.device.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RangeInfo {

    @ApiModelProperty(value = "终端类型 固定值:Terminal")
    private String devType = "Terminal";

    @ApiModelProperty(value = "序号")
    private Integer order;

    @ApiModelProperty(value = "设备列表和区域列表必填其一")
    private List<RangeDevInfo> rangeDevInfoList;

    @ApiModelProperty(value = "设备列表和区域列表必填其一")
    private List<RangeOrgInfo> rangeOrgInfoList;
}