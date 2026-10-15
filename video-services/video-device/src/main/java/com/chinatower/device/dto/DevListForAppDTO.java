package com.chinatower.device.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Name DevListForAppDTO
 * @Description
 * @Author lzt
 * @Date 2022/8/26
 */

@Data
public class DevListForAppDTO implements Serializable {
    @ApiModelProperty(value = "关键字")
    private String keyWords;

    @ApiModelProperty(value = "摄像机状态（0在线、1离线）")
    private String deviceStatus;

    @ApiModelProperty(value = "摄像机区域类型（1省份-2地市-3区县-4乡镇）")
    private String deviceType;

    @ApiModelProperty(value = "摄像机区域编码（省份-地市-区县-乡镇四级）")
    private String deviceArea;

    @ApiModelProperty(value = "摄像机厂家编码")
    private String modelCode;

    private String userId;

    private Integer pageNo;

    private Integer pageSize;

    //经度
    private String orderLong;

    //纬度
    private String orderLat;

    private List<String> modelCodeList;

    private String tenantId;

    private String industryCode;

    private String optFlag;
}
