package com.chinatower.industry.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 功能描述: 查询行业设备树请求实体
 *
 * @auther: hued
 * @date: 2022年8月11日10:04:35
 * @version: release_v1.5
 * @requestID:
 */
@Data
public class MonitorTreeInfReq extends BaseDataScopeEntity {
    /**
     * 关键字
     */
    @ApiModelProperty(value = "关键字", required = false)
    private String keyWords;
    /**
     * 是否收藏 1:收藏 0：未收藏
     */
    @ApiModelProperty(value = "是否收藏 1:收藏 0：未收藏", required = false)
    private String isCollection;
    /**
     * 展示方式 1：行政区域显示，2：租户组织展示 3：标签展示
     */
    @ApiModelProperty(value = "展示方式 1：行政区域显示，2：租户组织展示 3：标签展示", required = true)
    @NotNull(message = "展示方式不能为空")
    private Integer displayMode;

    /**
     * 经度
     */
    private String longitude;
    /**
     * 维度
     */
    private String latitude;

    /**
     * 查询半径（公里）
     */
    private String radius;

    /**
     * 设备类型:1-IPC 0-NVR;2-AR摄像头；3-热成像摄像头
     */
    private Integer categoryCode;
}
