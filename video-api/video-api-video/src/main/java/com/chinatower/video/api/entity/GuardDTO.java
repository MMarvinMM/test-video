package com.chinatower.video.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import javax.validation.constraints.NotNull;

/**
 * @ClassName GuardDTO
 * @Description 看守位参数
 * @Author chenshaowu
 * @Date 2022/2/21
 **/
@Data
@Alias("GuardDTOVideo")
public class GuardDTO extends DeviceSouthCommonDTO {

    /**
     * 订购编码
     *
     */
    @ApiModelProperty(value = "订购编码")
    private String orderCode;

    /**
     * 看守位名称
     * @required true
     */
    @ApiModelProperty(value = "看守位名称")
    @NotNull(message = "看守位名称不能为空")
    private String guardName;

    /**
     * 预置点id
     *
     */
    @ApiModelProperty(value = "预置点id")
    private String presetId;

    /**
     * 看守位id
     *
     */
    @ApiModelProperty(value = "看守位id")
    private String guardId;

    /**
     * 停留时间
     * @required true
     */
    @ApiModelProperty(value = "停留时间")
    @NotNull(message = "停留时间不能为空")
    private String staySeconds;

    /**
     * 看守位状态
     * @required true
     */
    @ApiModelProperty(value = "看守位状态")
    @NotNull(message = "看守位状态不能为空")
    private String guardStatus;

    /**
     * 受理节点编码
     *
     */
    @ApiModelProperty(value = "受理节点编码")
    private String acceptNode;

    /**
     * 接入节点编码
     *
     */
    @ApiModelProperty(value = "接入节点编码")
    private String accessNode;

}
