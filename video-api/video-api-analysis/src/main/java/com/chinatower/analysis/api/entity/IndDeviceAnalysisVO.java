package com.chinatower.analysis.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Name IndDeviceVO
 * @Description
 * @Author Chensw
 * @Date 2022/04/2022/4/1 16 : 09
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndDeviceAnalysisVO {

    /**
     * 设备总数
     * @required true
     */
    @ApiModelProperty("设备总数")
    private Integer deviceCountTotal;

    /**
     * 设备在线数量
     * @required true
     */
    @ApiModelProperty("设备在线数量")
    private Integer devCountOnline;

    /**
     * 设备离线数量
     * @required true
     */
    @ApiModelProperty("设备离线数量")
    private Integer devCountOffline;

    /**
     * 通道总数
     * @required true
     */
    @ApiModelProperty("通道总数")
    private Integer channelCountTotal;

    /**
     * 通道在线数量
     * @required true
     */
    @ApiModelProperty("通道在线数量")
    private Integer channelCountOnline;

    /**
     * 通道离线数量
     * @required true
     */
    @ApiModelProperty("通道离线数量")
    private Integer channelCountOffline;

}
