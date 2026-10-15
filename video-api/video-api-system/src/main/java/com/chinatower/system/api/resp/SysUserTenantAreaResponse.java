package com.chinatower.system.api.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @desription: 租户用户区域
 * @data 创建时间：2023/3/21 11:14
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class SysUserTenantAreaResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 县
     */
    @ApiModelProperty(value = "县")
    private String countyName;

    /**
     * 城市
     */
    @ApiModelProperty(value = "城市")
    private String cityName;

    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    private String provinceName;

    /**
     * 街道名称
     */
    @ApiModelProperty(value = "街道名称")
    private String townName;
}
