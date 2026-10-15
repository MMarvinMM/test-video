package com.chinatower.device.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cuibaoluo
 * @date 2025/12/15
 * @description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirHornReq {
    @ApiModelProperty(value = "设备编码")
    private String deviceCode;

    @ApiModelProperty(value = "喊话文件")
    private String hornUrl;

    @ApiModelProperty(value = "喊话类型 0 音频，1 文本")
    private String audioStatus;

    @ApiModelProperty(value = "文本喊话")
    private String hornText;

    @ApiModelProperty(value = "音量值 1-100的数字")
    private String playVolume;

    @ApiModelProperty(value = "挂载类型")
    private String mountType;

    @ApiModelProperty(value = "接入节点")
    private String accessNode;

    @ApiModelProperty(value = "受理节点")
    private String acceptNode;
    @ApiModelProperty(value = "md5")
    private String md5;
    @ApiModelProperty(value = "single:单次播放，cycle:循环播放")
    private String playType;

}
