package com.chinatower.common.core.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Description 行业应用业务实体基类
 * @Author guyuefei
 * @Date 2022/7/14
 * @Version release_v1.5
 **/
@Data
public class BaseIndustryEntity extends BasePlatformEntity {
    /** 租户id */
    @ApiModelProperty("租户id")
    private String tenantId;
    /** 用户id */
    @ApiModelProperty("用户id")
    private String userId;
    /** 应用版本编码 */
    @ApiModelProperty("应用版本编码")
    private String appVerCode;
    /** 行业编码 */
    @ApiModelProperty("行业编码")
    private String industryCode;
    /** 平台编码 */
    @ApiModelProperty("平台编码")
    private String platformCode;
}
