package com.chinatower.device.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SensorDeviceCountVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总数量
     * @required true
     */
    @ApiModelProperty("总数量")
    private Integer totalCount;

    /**
     * 在线数量
     * @required true
     */
    @ApiModelProperty("在线数量")
    private Integer onlineCount;

    /**
     * 离线数量
     * @required true
     */
    @ApiModelProperty("离线数量")
    private Integer offlineCount;
}
