package com.chinatower.industry.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 功能描述: 摄像机查询实体类
 *
 * @auther: hued
 * @date: 2022年8月11日10:04:35
 * @version: release_v1.5
 * @requestID:
 */
@Data
public class DeviceLayerReq extends BaseDataScopeEntity {
    /**
     * 经度
     */
    @ApiModelProperty(value = "经度", required = false)
//    @NotNull(message = "经度不能为空")
    private Double longitude;
    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度", required = false)
//    @NotNull(message = "维度不能为空")
    private Double latitude;
    /**
     * 查询范围
     */
    @ApiModelProperty(value = "查询范围", required = false)
//    @NotBlank(message = "查询范围不能为空")
    private String distance;
    /**
     * 省编码
     */
    @ApiModelProperty(value = "省编码", required = false)
    private String provinceId;
    /**
     * 市编码
     */
    @ApiModelProperty(value = "市编码", required = false)
    private String cityId;
    /**
     * 区编码
     */
    @ApiModelProperty(value = "区编码", required = false)
    private String countyId;

    /**
     * 设备类型:1-IPC 0-NVR;2-AR摄像头；3-热成像摄像头
     */
    @ApiModelProperty(value = "设备类型", required = false)
    private Integer categoryCode;

    /**
     * sql语句中的limit参数
     */
    private Integer lmt;
    /**
     * 播放实时视频路数
     */
    @ApiModelProperty(value = "播放实时视频路数", required = false)
    private int urlNum;
    /**
     * 攝像機编码列表
     */
    @ApiModelProperty(value = "攝像機编码列表", required = false)
    private List<String> deviceCodes;


    /**
     * 是否查询的是伐木场的图层信息 1为是 空或其他为不是
     */
    @ApiModelProperty(value = "是否查询的是伐木场的图层信息", required = false)
    private Integer isWoodProcessingPlant;

    @ApiModelProperty(value = "分页参数", required = false)
    private Integer pageSize;

    @ApiModelProperty(value = "分页参数", required = false)
    private Integer pageNum;

    @ApiModelProperty(value = "关键字", required = false)
    private String keyWords;

    private List<String> siteCodeList;

}
