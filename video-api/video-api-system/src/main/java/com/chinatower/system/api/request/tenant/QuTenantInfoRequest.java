package com.chinatower.system.api.request.tenant;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @auther liuhuanhuan
 * @date 2022/2/10
 */
@Data
@ApiModel(value = "QuTenantInfoRequest",description = "租户查询请求参数")
public class QuTenantInfoRequest extends BaseDataScopeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户编码
     */
    @ApiModelProperty(value = "租户编码", example = "TEN10001")
    private String tenantId;

    /**
     * 租户名称
     */
    @ApiModelProperty(value = "租户名称", example = "测试租户")
    private String tenantName;

    /*@ApiModelProperty(value = "行业信息", example = "[\"1\", \"2\", \"3\"]")
    private String[] tradeInfo;*/

    /**
     * 省份id
     */
    @ApiModelProperty(value = "省份id", example = "110000")
    private String provinceCode;

    /**
     * 地市id
     */
    @ApiModelProperty(value = "地市id", example = "110100")
    private String cityCode;

    /**
     * 区县id
     */
    @ApiModelProperty(value = "区县id", example = "110101")
    private String countyCode;

    /**
     * 页码
     */
    @ApiModelProperty(value = "页码", example = "1")
    private Integer pageNum;

    /**
     * 每页大小
     */
    @ApiModelProperty(value = "每页大小", example = "10")
    private Integer pageSize;
}
