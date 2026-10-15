package com.chinatower.industry.vo;

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
public class IndDeviceVO {

    @ApiModelProperty("设备总数")
    private Integer deviceCountTotal;

    @ApiModelProperty("设备在线数量")
    private Integer devCountOnline;

    @ApiModelProperty("设备离线数量")
    private Integer devCountOffline;

    @ApiModelProperty("通道总数")
    private Integer channelCountTotal;

    @ApiModelProperty("通道在线数量")
    private Integer channelCountOnline;

    @ApiModelProperty("通道离线数量")
    private Integer channelCountOffline;

}
