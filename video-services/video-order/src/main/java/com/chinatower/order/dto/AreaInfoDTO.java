package com.chinatower.order.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AreaInfoDTO
 * @Deacription
 * @Author chenmf
 * @Date 2022/4/19 17:21
 * @Version 1.0
 **/
@Data
public class AreaInfoDTO implements Serializable {
    
    /**
     * 省份编码
     */
    @ApiModelProperty(value = "省份编码")
    private String provinceId;
    
    /**
     * 地市编码
     */
    @ApiModelProperty(value = "地市编码")
    private String cityId;
    
    /**
     * 区县编码
     */
    @ApiModelProperty(value = "区县编码")
    private String countyId;
    
    /**
     * 省份名称
     */
    @ApiModelProperty(value = "省份名称")
    private String provinceName;
    
    /**
     * 地市名称
     */
    @ApiModelProperty(value = "地市名称")
    private String cityName;
    
    /**
     * 区县名称
     */
    @ApiModelProperty(value = "区县名称")
    private String countyName;
}
