package com.chinatower.platform.entity.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @desription: 查询租户下城市天气质量排行
 * @data 创建时间：2023/8/7 16:03
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class TenantWeatherRankReq {

    @ApiModelProperty(value = "租户ID")
    private String tenantId;

    @ApiModelProperty(value = "行业")
    private String industryCode;

    @ApiModelProperty(value = "区域编码")
    private String adCode;
}
