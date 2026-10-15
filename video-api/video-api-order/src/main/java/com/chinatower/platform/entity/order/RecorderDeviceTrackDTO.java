package com.chinatower.platform.entity.order;

import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.chinatower.platform.constants.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "执法记录仪遥控信息",description = "执法记录仪遥控信息")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecorderDeviceTrackDTO extends BaseMongoDTO implements Serializable {

    /**
     * 轨迹时间点
     */
    @ApiModelProperty(value = "轨迹时间点")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date time;

    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    private String longitude;

    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度")
    private String latitude;

    /**
     * 速度
     */
    @ApiModelProperty(value = "速度")
    private String speed;

    /**
     * 方向
     */
    @ApiModelProperty(value = "方向")
    private String direction;

    /**
     * 海拔高度 单位：m
     */
    @ApiModelProperty(value = "海拔高度 单位：m")
    private String altitude;

    /**
     * 设备编码
     */
    @ApiModelProperty(value = "设备编码")
    private String deviceCode;

    /**
     * 通道编码
     */
    @ApiModelProperty(value = "通道编码")
    private String channelCode;

    /**
     * 接入节点
     */
    @ApiModelProperty(value = "接入节点")
    private String accessNode;

}
