package com.chinatower.device.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 无人机指点飞行查询请求DTO
 */
@Data
public class UavPointToFlyQueryReqDTO extends BaseDataScopeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 飞行目的地的经度
     */
    @ApiModelProperty(value = "飞行目的地的经度", required = true)
    private String longitude;

    /**
     * 飞行目的地的纬度
     */
    @ApiModelProperty(value = "飞行目的地的纬度", required = true)
    private String latitude;

    /**
     * 搜索半径（单位：米）
     */
    private String radius;

    /**
     * 无人机编码
     */
    private String deviceCode;
}
