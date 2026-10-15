package com.chinatower.system.api.request.tenant;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @auther liuhuanhuan
 * @date 2022/3/17
 */
@Data
@ApiModel(value = "TenantTradeRequest",description = "租户行业信息新增请求参数")
public class TenantTradeRequest implements Serializable {
    private static final long serialVersionUID=1L;

    /**
     * 行业编码
     */
    @ApiModelProperty(value = "行业编码",required = true, example = "100000")
    private String industryCode;

    /**
     * 版本编码
     */
    @ApiModelProperty(value = "版本编码",required = true, example = "6135b3913f4473")
    private String appVerCode;

    /**
     * 行业名称
     */
    private String industryName;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TenantTradeRequest request = (TenantTradeRequest) o;
        return Objects.equals(industryCode, request.industryCode) &&
                Objects.equals(appVerCode, request.appVerCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(industryCode, appVerCode);
    }

    public boolean isEmpty() {
        return StrUtil.isEmpty(this.industryCode) || StrUtil.isEmpty(this.appVerCode);
    }
}
